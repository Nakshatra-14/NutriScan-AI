export default function Features() {
  return (
    <section className="mt-28 grid grid-cols-1 md:grid-cols-3 gap-8 px-8 mb-20">
      {[
        { title: "📷 Barcode Scan", desc: "Use your camera to instantly scan food barcodes and fetch nutrition data." },
        { title: "🍩 Nutrition Facts", desc: "Get details like sugar, fats, calories, and ingredients in real time." },
        { title: "💡 Health Insights", desc: "AI-driven tips on whether a food is healthy or should be avoided." }
      ].map((item, idx) => (
        <div key={idx} className="p-6 rounded-2xl backdrop-blur-xl bg-white/10 hover:scale-105 transition shadow-lg">
          <h3 className="text-2xl font-bold mb-3">{item.title}</h3>
          <p>{item.desc}</p>
        </div>
      ))}
    </section>
  );
}
