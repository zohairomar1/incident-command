import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';

interface Props {
  data: Record<string, number>;
}

const colors: Record<string, string> = {
  OPEN: '#ef4444',
  ACKNOWLEDGED: '#eab308',
  RESOLVED: '#22c55e',
  CLOSED: '#6b7280',
};

export default function StatusChart({ data }: Props) {
  const chartData = Object.entries(data).map(([name, value]) => ({ name, value }));

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-5">
      <h3 className="mb-4 text-sm font-medium text-gray-700">By Status</h3>
      <ResponsiveContainer width="100%" height={250}>
        <PieChart>
          <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
            {chartData.map((entry) => (
              <Cell key={entry.name} fill={colors[entry.name] || '#3b82f6'} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
