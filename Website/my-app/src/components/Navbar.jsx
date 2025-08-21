export default function Navbar() {
  return (
    <nav className="flex justify-between items-center px-8 py-5 backdrop-blur-lg bg-white/10 rounded-xl mx-6 mt-4 shadow-lg">
      <h1 className="text-2xl font-extrabold tracking-wider">🍭 FoodScan</h1>
      <ul className="hidden md:flex space-x-8 text-lg font-medium">
        <li className="hover:text-yellow-300 cursor-pointer">Home</li>
        <li className="hover:text-yellow-300 cursor-pointer">Features</li>
        <li className="hover:text-yellow-300 cursor-pointer">Demo</li>
        <li className="hover:text-yellow-300 cursor-pointer">Contact</li>
      </ul>
      <button className="px-4 py-2 rounded-lg bg-yellow-400 text-black font-bold shadow-lg hover:scale-105 transition">
        Get Started
      </button>
    </nav>
  );
}
