import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface Props {
  data: Record<string, number>;
}

const colors: Record<string, string> = { P1: '#ef4444', P2: '#f97316', P3: '#eab308', P4: '#3b82f6' };

export default function SeverityChart({ data }: Props) {
  const chartData = Object.entries(data).map(([name, count]) => ({ name, count }));

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-5">
      <h3 className="mb-4 text-sm font-medium text-gray-700">By Severity</h3>
      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" fill="#3b82f6" radius={[4, 4, 0, 0]}>
            {chartData.map((entry) => (
              <rect key={entry.name} fill={colors[entry.name] || '#3b82f6'} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
