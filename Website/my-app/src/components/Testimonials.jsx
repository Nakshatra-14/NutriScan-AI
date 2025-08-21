export default function Testimonials() {
  return (
    <section className="px-8 mb-20 text-center">
      <h2 className="text-4xl font-bold mb-10">💬 What People Say</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {[
          { name: "Rahul", review: "Super useful! I scan snacks daily before eating." },
          { name: "Priya", review: "Helped me avoid unhealthy food. The insights are great!" },
          { name: "Arjun", review: "Beautiful UI and fast scanning experience." }
        ].map((t, i) => (
          <div key={i} className="p-6 rounded-2xl bg-white/10 shadow-lg">
            <p className="italic">"{t.review}"</p>
            <h4 className="mt-3 font-bold">- {t.name}</h4>
          </div>
        ))}
      </div>
    </section>
  );
}
