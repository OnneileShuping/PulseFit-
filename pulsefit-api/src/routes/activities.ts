import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../lib/prisma';
import { requireAuth } from '../middleware/auth';

const router = Router();

const activitySchema = z.object({
  type: z.string(),
  durationSeconds: z.number().int().nonnegative(),
  distanceKm: z.number().nonnegative(),
  calories: z.number().int().nonnegative(),
  notes: z.string().optional(),
  timestamp: z.string().datetime().or(z.number()),
});

// XP calculator — mirrors the client logic
function calculateXp(calories: number): number {
  return Math.floor(calories / 2);
}

// GET /api/activities?since=ISO
router.get('/', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const since = req.query.since as string | undefined;

  const where: any = { userId };
  if (since) {
    try {
      where.timestamp = { gte: new Date(since) };
    } catch {
      // ignore invalid date
    }
  }

  const activities = await prisma.activity.findMany({
    where,
    orderBy: { timestamp: 'desc' },
  });

  return res.json({ activities });
});

// POST /api/activities
router.post('/', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const parse = activitySchema.safeParse(req.body);
  if (!parse.success) {
    return res.status(400).json({ error: parse.error.flatten() });
  }

  const { type, durationSeconds, distanceKm, calories, notes, timestamp } = parse.data;

  const activity = await prisma.activity.create({
    data: {
      userId,
      type,
      durationSeconds,
      distanceKm,
      calories,
      notes: notes ?? '',
      timestamp: new Date(timestamp),
      syncStatus: 'synced',
    },
  });

  // Award XP
  const xpGained = calculateXp(calories);
  const updatedUser = await prisma.user.update({
    where: { id: userId },
    data: { xp: { increment: xpGained } },
  });

  // Recalculate tier
  let tier = 'Bronze';
  if (updatedUser.xp >= 5000) tier = 'Platinum';
  else if (updatedUser.xp >= 3000) tier = 'Gold';
  else if (updatedUser.xp >= 1000) tier = 'Silver';

  await prisma.user.update({ where: { id: userId }, data: { tier } });

  return res.status(201).json({
    activity,
    xpGained,
    newBadges: [],
    tier,
  });
});

// POST /api/activities/sync — offline batch upload
router.post('/sync', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const { activities = [], deletedIds = [] } = req.body || {};

  const created: any[] = [];
  for (const a of activities) {
    const parse = activitySchema.safeParse(a);
    if (!parse.success) continue;

    const createdActivity = await prisma.activity.create({
      data: {
        userId,
        type: parse.data.type,
        durationSeconds: parse.data.durationSeconds,
        distanceKm: parse.data.distanceKm,
        calories: parse.data.calories,
        notes: parse.data.notes ?? '',
        timestamp: new Date(parse.data.timestamp),
        syncStatus: 'synced',
      },
    });
    created.push(createdActivity);
  }

  if (deletedIds.length > 0) {
    await prisma.activity.deleteMany({
      where: { id: { in: deletedIds }, userId },
    });
  }

  const xpGained = created.reduce((sum, a) => sum + calculateXp(a.calories), 0);
  if (xpGained > 0) {
    await prisma.user.update({
      where: { id: userId },
      data: { xp: { increment: xpGained } },
    });
  }

  return res.json({
    serverActivities: created,
    conflicts: [],
    xpGained,
    newBadges: [],
  });
});

export default router;