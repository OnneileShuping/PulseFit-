import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import authRoutes from './routes/auth';
import userRoutes from './routes/users';
import activityRoutes from './routes/activities';
import badgeRoutes from './routes/badges';
import squadRoutes from './routes/squads';
import aiRoutes from './routes/ai';

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '1mb' }));

// Health check
app.get('/', (_req, res) => {
  res.json({ status: 'ok', app: 'PulseFit API', version: '1.0.0' });
});

app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/activities', activityRoutes);
app.use('/api/badges', badgeRoutes);
app.use('/api/squads', squadRoutes);
app.use('/api/ai', aiRoutes);

// 404
app.use((_req, res) => {
  res.status(404).json({ error: 'Endpoint not found' });
});

const PORT = Number(process.env.PORT) || 3000;
app.listen(PORT, () => {
  console.log(`PulseFit API listening on port ${PORT}`);
});