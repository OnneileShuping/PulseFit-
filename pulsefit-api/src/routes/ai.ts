import { Router } from 'express';
import { prisma } from '../lib/prisma';
import { requireAuth } from '../middleware/auth';

const router = Router();

// GET /api/ai/insights — rule-based recommendations
router.get('/insights', requireAuth, async (req, res) => {
  const userId = req.user!.userId;

  const last7Days = new Date();
  last7Days.setDate(last7Days.getDate() - 7);

  const activities = await prisma.activity.findMany({
    where: { userId, timestamp: { gte: last7Days } },
    orderBy: { timestamp: 'desc' },
  });

  const totalMinutes = activities.reduce((s, a) => s + a.durationSeconds / 60, 0);
  const totalCalories = activities.reduce((s, a) => s + a.calories, 0);

  // Simple heuristic
  let trainingLoad = 'Low';
  if (totalMinutes > 240) trainingLoad = 'High';
  else if (totalMinutes > 120) trainingLoad = 'Moderate';

  let fatigue = 'Low';
  if (activities.length > 5 && totalMinutes > 240) fatigue = 'High';
  else if (activities.length > 3) fatigue = 'Moderate';

  let recommendation = 'Keep up the great work — your training is on track.';
  if (fatigue === 'High') {
    recommendation =
      'Take a rest day or light stretch — your body is primed for recovery. A 15-minute mobility session will keep you on track without adding strain.';
  } else if (activities.length === 0) {
    recommendation = 'You have not logged a workout yet this week. Start with a 20-minute walk or jog.';
  }

  return res.json({
    trainingLoad,
    fatigue,
    recommendation,
    weeklyMinutes: Math.round(totalMinutes),
    weeklyCalories: totalCalories,
    sessions: activities.length,
  });
});

export default router;