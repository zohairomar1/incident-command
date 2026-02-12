import type { MetricsResponse } from '../../types';

interface Props {
  metrics: MetricsResponse;
}

export default function SummaryCards({ metrics }: Props) {
  const cards = [
    { label: 'Total Incidents', value: metrics.totalIncidents, color: 'text-gray-900' },
    { label: 'Open', value: metrics.openIncidents, color: 'text-red-600' },
    { label: 'Resolved', value: metrics.resolvedIncidents, color: 'text-green-600' },
    { label: 'MTTR (min)', value: Math.round(metrics.meanTimeToResolveMinutes), color: 'text-blue-600' },
  ];

  return (
    <div className="grid grid-cols-4 gap-4">
      {cards.map((card) => (
        <div key={card.label} className="rounded-xl border border-gray-200 bg-white p-5">
          <p className="text-sm text-gray-500">{card.label}</p>
          <p className={`mt-1 text-3xl font-bold ${card.color}`}>{card.value}</p>
        </div>
      ))}
    </div>
  );
}
