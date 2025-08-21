export default function Steps() {
  return (
    <section className="px-8 text-center mb-20">
      <h2 className="text-4xl font-bold mb-10">🚀 How It Works</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
        <div className="p-6 rounded-xl bg-white/10 shadow-lg">
          <h3 className="text-xl font-bold mb-2">Step 1: Scan</h3>
          <p>Open the app and scan the barcode of any food item.</p>
        </div>
        <div className="p-6 rounded-xl bg-white/10 shadow-lg">
          <h3 className="text-xl font-bold mb-2">Step 2: Analyze</h3>
          <p>Our AI extracts nutrition values from global databases.</p>
        </div>
        <div className="p-6 rounded-xl bg-white/10 shadow-lg">
          <h3 className="text-xl font-bold mb-2">Step 3: Decide</h3>
          <p>Get instant insights on whether it’s healthy or not.</p>
        </div>
      </div>
    </section>
  );
}
