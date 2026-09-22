import { Router } from 'express';
import { z } from 'zod';
import { prisma } from '../lib/prisma';
import { hashPassword, comparePassword } from '../utils/password';
import { signToken } from '../utils/jwt';

const router = Router();

const registerSchema = z.object({
  email: z.string().email(),
  username: z.string().min(2).max(50),
  password: z.string().min(6),
});

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(1),
});

// POST /api/auth/register
router.post('/register', async (req, res) => {
  const parse = registerSchema.safeParse(req.body);
  if (!parse.success) {
    return res.status(400).json({ error: parse.error.flatten() });
  }

  const { email, username, password } = parse.data;

  const existing = await prisma.user.findUnique({ where: { email } });
  if (existing) {
    return res.status(409).json({ error: 'Email already registered' });
  }

  const passwordHash = await hashPassword(password);
  const user = await prisma.user.create({
    data: {
      email,
      username,
      passwordHash,
      settings: {
        create: {}, // default settings
      },
    },
  });

  const token = signToken({ userId: user.id, email: user.email });

  return res.status(201).json({
    token,
    user: {
      id: user.id,
      username: user.username,
      email: user.email,
      xp: user.xp,
      tier: user.tier,
    },
  });
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  const parse = loginSchema.safeParse(req.body);
  if (!parse.success) {
    return res.status(400).json({ error: parse.error.flatten() });
  }

  const { email, password } = parse.data;
  const user = await prisma.user.findUnique({ where: { email } });

  if (!user || !(await comparePassword(password, user.passwordHash))) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  const token = signToken({ userId: user.id, email: user.email });

  return res.json({
    token,
    user: {
      id: user.id,
      username: user.username,
      email: user.email,
      xp: user.xp,
      tier: user.tier,
    },
  });
});

export default router;