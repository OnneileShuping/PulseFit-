import { Router } from 'express';
import { prisma } from '../lib/prisma';
import { requireAuth } from '../middleware/auth';

const router = Router();

// GET /api/badges
router.get('/', requireAuth, async (req, res) => {
  const userId = req.user!.userId;

  const allBadges = await prisma.badge.findMany();
  const earned = await prisma.userBadge.findMany({ where: { userId } });

  const earnedIds = new Set(earned.map((e) => e.badgeId));

  return res.json({
    badges: allBadges.map((b) => ({
      id: b.id,
      name: b.name,
      description: b.description,
      criteria: b.criteria,
      earned: earnedIds.has(b.id),
    })),
  });
});

export default router;