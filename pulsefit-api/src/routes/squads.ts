import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../lib/prisma';
import { requireAuth } from '../middleware/auth';

const router = Router();

const createSquadSchema = z.object({
  name: z.string().min(2).max(50),
  maxMembers: z.number().int().min(2).max(6).default(6),
});

// GET /api/squads
router.get('/', requireAuth, async (_req, res) => {
  const squads = await prisma.squad.findMany({
    include: { _count: { select: { members: true } } },
  });

  return res.json({
    squads: squads.map((s) => ({
      id: s.id,
      name: s.name,
      maxMembers: s.maxMembers,
      ownerId: s.ownerId,
      memberCount: s._count.members,
    })),
  });
});

// POST /api/squads
router.post('/', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const parse = createSquadSchema.safeParse(req.body);
  if (!parse.success) {
    return res.status(400).json({ error: parse.error.flatten() });
  }

  const squad = await prisma.squad.create({
    data: {
      name: parse.data.name,
      maxMembers: parse.data.maxMembers,
      ownerId: userId,
      members: { create: { userId } },
    },
  });

  return res.status(201).json({ squad });
});

// POST /api/squads/:id/join
router.post('/:id/join', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const squadId = req.params.id;

  const squad = await prisma.squad.findUnique({
    where: { id: squadId },
    include: { _count: { select: { members: true } } },
  });

  if (!squad) return res.status(404).json({ error: 'Squad not found' });
  if (squad._count.members >= squad.maxMembers) {
    return res.status(400).json({ error: 'Squad is full' });
  }

  try {
    await prisma.squadMember.create({ data: { squadId, userId } });
    return res.json({ message: 'Joined successfully' });
  } catch {
    return res.status(409).json({ error: 'Already a member' });
  }
});

// GET /api/squads/:id/leaderboard
router.get('/:id/leaderboard', requireAuth, async (req, res) => {
  const squadId = req.params.id;

  const members = await prisma.squadMember.findMany({
    where: { squadId },
    include: { user: true },
  });

  // Simple leaderboard by XP
  const leaderboard = members
    .map((m) => ({
      userId: m.user.id,
      username: m.user.username,
      xp: m.user.xp,
      tier: m.user.tier,
    }))
    .sort((a, b) => b.xp - a.xp);

  return res.json({ leaderboard });
});

export default router;