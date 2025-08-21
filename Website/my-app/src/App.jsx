import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import Spline from "@splinetool/react-spline";


export default function App() {
  const videoRef = useRef(null);
  const [camError, setCamError] = useState("");
  const [manualCode, setManualCode] = useState("");
  const [fakeResult, setFakeResult] = useState("");

  // Camera preview
  useEffect(() => {
    let stream;
    (async () => {
      try {
        stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: { ideal: "environment" } },
          audio: false,
        });
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } catch (e) {
        setCamError("Camera not available (use HTTPS / allow permission).");
      }
    })();
    return () => stream && stream.getTracks().forEach((t) => t.stop());
  }, []);

  const simulateScan = () => {
    const code = manualCode.trim() || "8901234567890";
    setFakeResult(
      `Barcode: ${code}
Product: Sample Granola (per 100g)
Protein 10g | Carbs 60g | Sugars 18g | Fat 8g | Fiber 7g
GI: 55   NutriScore: B   EcoScore: B
Health Label: OK
Tips: Prefer <10g sugar/100g • Add yogurt/nuts for protein • Good fiber for satiety`
    );
  };

  return (
    <div className="relative min-h-screen bg-black text-white overflow-hidden">
      

      {/* ===== NAVBAR ===== */}
      <nav className="flex justify-between items-center p-6 text-sm md:text-base bg-black/50 backdrop-blur-md sticky top-0 z-20">
        <span className="text-2xl md:text-3xl font-extrabold bg-gradient-to-r from-[#FF6A00] via-white to-[#0DA34E] bg-clip-text text-transparent">
          NutriScan
        </span>
        <div className="space-x-5 md:space-x-8">
          <a href="#features" className="hover:text-orange-400">Features</a>
          <a href="#steps" className="hover:text-green-400">How it works</a>
          <a href="#scanner" className="hover:text-orange-400">Scanner</a>
          <a href="#gallery" className="hover:text-green-400">Gallery</a>
          <a href="#testimonials" className="hover:text-orange-400">Testimonials</a>
        </div>
      </nav>

      {/* ===== HERO ===== */}
      <section id="hero" className="relative w-full h-screen flex items-center justify-center text-center">
        {/* Spline background */}
        <div className="absolute inset-0 z-0">
        <Spline
        scene="https://prod.spline.design/YkzsBlZNYIb-Bwsr/scene.splinecode" 
      />
        </div>

        {/* Hero text (in front) */}
        <div className="relative z-10 px-6">
          <h1 className="text-4xl md:text-6xl font-extrabold mb-6 bg-gradient-to-r from-[#FF6A00] via-white to-[#0DA34E] bg-clip-text text-transparent drop-shadow-[0_0_15px_rgba(255,255,255,0.6)]">
            Welcome to NUTRI-INFO
          </h1>
          <p className="max-w-2xl mx-auto text-gray-200">
            Instant nutrient breakdown, GI, NutriScore, EcoScore, a clear label (Good/OK/Bad) and 5–6 tips tailored to you.
          </p>
          <a
            href="#scanner"
            className="inline-block mt-8 px-6 py-3 rounded-2xl font-semibold bg-gradient-to-r from-[#FF6A00] to-[#0DA34E] hover:scale-105 transition"
          >
            Try the Scanner
          </a>
        </div>
      </section>
      

      {/* ===== FEATURES ===== */}
      <section id="features" className="relative w-full h-screen flex items-center justify-center text-center">
         {/* Spline background */}
        <div className="absolute inset-0 z-0">
       <Spline scene="https://prod.spline.design/gbzQMsk9XtEZwjTS/scene.splinecode" />
        </div>
      <motion.section
        id="features"
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="px-6 md:px-10 py-12 md:py-16 grid md:grid-cols-3 gap-6 relative z-10"
      >
        {[
          [" Instant Analysis", "Protein, carbs, fats, sugars, fiber, GI, NutriScore, EcoScore."],
          [" Risk Signals", "Flags high sugar/carbs for diabetes-friendly decisions."],
          [" Multilingual TTS", "English • Hindi • Bengali for wider accessibility."],
          [" Explainable", "Shows why each tip was generated (Education Mode)."],
          [" Care Mode", "Share scans, alerts for parents/caregivers."],
          ["🎚 Personalization", "Age, conditions, and dietary preferences aware."],
        ].map(([t, d], i) => (
          <motion.div
            key={i}
            whileHover={{ scale: 1.04 }}
            className="p-6 rounded-2xl bg-white/10 backdrop-blur-lg shadow-lg"
          >
            <h3 className="text-xl font-bold mb-2">{t}</h3>
            <p className="text-gray-300">{d}</p>
          </motion.div>
        ))}
      </motion.section></section>

      {/* ===== STEPS ===== */}
      <motion.section
        id="steps"
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="px-6 md:px-10 py-12 md:py-16 text-center relative z-10"
      >
        <h2 className="text-3xl md:text-4xl font-bold mb-8 bg-gradient-to-r from-[#FF6A00] via-white to-[#0DA34E] bg-clip-text text-transparent">
          How it works
        </h2>
        <div className="grid md:grid-cols-3 gap-6">
          <div className="p-6 bg-white/10 rounded-xl">1) Open the scanner</div>
          <div className="p-6 bg-white/10 rounded-xl">2) Point at barcode</div>
          <div className="p-6 bg-white/10 rounded-xl">3) Get scores & tips</div>
        </div>
      </motion.section>

      {/* ===== SCANNER ===== */}
      <motion.section
        id="scanner"
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="px-6 md:px-10 py-12 md:py-16 text-center relative z-10"
      >
        <h2 className="text-3xl md:text-4xl font-bold mb-6 bg-gradient-to-r from-[#FF6A00] via-white to-[#0DA34E] bg-clip-text text-transparent">
          Barcode Scanner
        </h2>
        <div className="mx-auto max-w-xl p-4 bg-gray-900 rounded-2xl border border-white/20">
          <video
            ref={videoRef}
            className="w-full h-64 bg-black rounded-lg object-cover"
            autoPlay
            playsInline
            muted
          />
          {camError && <p className="mt-3 text-red-400 text-sm">{camError}</p>}
          <div className="mt-4 grid grid-cols-1 md:grid-cols-[1fr_auto] gap-3">
            <input
              value={manualCode}
              onChange={(e) => setManualCode(e.target.value)}
              placeholder="Or enter barcode (e.g., 8901234567890)"
              className="p-3 rounded-lg text-black"
            />
            <button
              onClick={simulateScan}
              className="px-5 py-3 rounded-lg bg-gradient-to-r from-[#FF6A00] to-[#0DA34E] font-semibold hover:scale-105 transition"
            >
              Analyze
            </button>
          </div>
          {fakeResult && (
            <div className="mt-4 p-4 text-left bg-white/10 rounded-lg whitespace-pre-wrap">
              {fakeResult}
            </div>
          )}
        </div>
        <p className="mt-4 text-sm text-gray-400">
          (Frontend preview only. Plug in your offline ML & OpenCV to decode and classify.)
        </p>
      </motion.section>

      {/* ===== GALLERY ===== */}
      <section id="gallery" className="py-16 px-6 md:px-12 lg:px-24 bg-black/70 backdrop-blur-md relative z-10">
        <h2 className="text-4xl font-extrabold text-center bg-gradient-to-r from-orange-500 via-white to-green-500 bg-clip-text text-transparent mb-12">
          Nutrition Gallery
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
          {["healthy-food", "fruits", "vegetables", "salad", "smoothie", "protein"].map((tag, i) => (
            <motion.img
              key={i}
              whileHover={{ scale: 1.1 }}
              src={`https://source.unsplash.com/400x300/?${tag}`}
              alt={tag}
              className="rounded-2xl shadow-lg"
            />
          ))}
        </div>
      </section>

      {/* ===== TESTIMONIALS ===== */}
      <motion.section
        id="testimonials"
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7 }}
        className="px-6 md:px-10 py-12 md:py-16 relative z-10"
      >
        <h2 className="text-3xl md:text-4xl font-bold mb-8 text-center bg-gradient-to-r from-[#FF6A00] via-white to-[#0DA34E] bg-clip-text text-transparent">
          What users say
        </h2>
        <div className="grid md:grid-cols-3 gap-6">
          {[
            ["Riya", "I finally understand labels. The tips are short and useful!"],
            ["Arjun", "Quick risk signals help me manage my sugar better."],
            ["Meera", "Love the voice in Hindi for my parents."],
          ].map(([n, q], i) => (
            <div key={i} className="p-6 bg-white/10 rounded-2xl">
              <p className="italic mb-3">“{q}”</p>
              <p className="font-semibold text-orange-300">— {n}</p>
            </div>
          ))}
        </div>
      </motion.section>

      {/* ===== FOOTER ===== */}
      <footer id="footer" className="px-6 md:px-10 py-10 text-center bg-black/60 backdrop-blur-md relative z-10">
        <p className="text-gray-400">
          © {new Date().getFullYear()} NutriScan — Instant, explainable nutrition guidance.
        </p>
      </footer>
    </div>
  );
}
