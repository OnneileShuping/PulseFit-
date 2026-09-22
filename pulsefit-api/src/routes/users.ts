import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../lib/prisma';
import { requireAuth } from '../middleware/auth';

const router = Router();

const settingsSchema = z.object({
  units: z.string().optional(),
  theme: z.string().optional(),
  language: z.string().optional(),
  notificationsEnabled: z.boolean().optional(),
});

// GET /api/users/me
router.get('/me', requireAuth, async (req, res) => {
  const userId = req.user!.userId;

  const user = await prisma.user.findUnique({
    where: { id: userId },
    include: {
      settings: true,
      userBadges: { include: { badge: true } },
    },
  });

  if (!user) return res.status(404).json({ error: 'User not found' });

  return res.json({
    user: {
      id: user.id,
      username: user.username,
      email: user.email,
      xp: user.xp,
      tier: user.tier,
    },
    badges: user.userBadges.map((ub) => ({
      id: ub.badge.id,
      name: ub.badge.name,
      description: ub.badge.description,
      earnedAt: ub.earnedAt,
    })),
    settings: user.settings,
  });
});

// PUT /api/users/me/settings
router.put('/me/settings', requireAuth, async (req, res) => {
  const userId = req.user!.userId;
  const parse = settingsSchema.safeParse(req.body);
  if (!parse.success) {
    return res.status(400).json({ error: parse.error.flatten() });
  }

  const settings = await prisma.userSettings.upsert({
    where: { userId },
    update: parse.data,
    create: { userId, ...parse.data },
  });

  return res.json({ updatedSettings: settings });
});

export default router;