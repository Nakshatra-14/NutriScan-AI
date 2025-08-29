document.addEventListener('DOMContentLoaded', function() {
	const container = document.getElementById('result-container');
	const title = document.getElementById('product-title');

	function render(result) {
		if (!result) {
			container.innerHTML = '<p>Nothing to display. Go back and scan a product.</p>';
			return;
		}
		title.textContent = result.nutrients?.product_name || 'Scan Result';
		const rating = result.rating || '-';
		const gi = result.glycemic_index ?? '-';
		const eco = result.ecoscore ?? '-';
		const n = result.nutrients || {};

		container.innerHTML = `
			<div class="dashboard-card">
				<div class="result-header">
					<img class="result-photo" src="${n.image || ''}" alt="Product" onerror="this.style.display='none'"/>
					<div>
						<h3 style="margin:0 0 6px 0;">Overview</h3>
						<p><strong>Brand:</strong> ${n.brand || '-'} | <strong>Quantity:</strong> ${n.quantity || '-'}</p>
						<p><strong>Rating:</strong> <span class="badge">${rating}</span> | <strong>GI:</strong> ${gi} | <strong>EcoScore:</strong> ${eco}</p>
						<div class="rating-meter" aria-label="Rating meter" style="margin-top:8px;">
							<div class="rating-fill rating-${rating}"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="dashboard-card">
				<h3>Nutrients (per 100g)</h3>
				<ul>
					<li>Energy: ${val(n.energy_kcal_100g)} kcal</li>
					<li>Carbohydrates: ${val(n.carbohydrates_100g)} g (Sugars: ${val(n.sugars_100g)} g)</li>
					<li>Protein: ${val(n.proteins_100g)} g</li>
					<li>Fat: ${val(n.fat_100g)} g (Sat: ${val(n.saturated_fat_100g)} g)</li>
					<li>Fiber: ${val(n.fiber_100g)} g</li>
					<li>Salt: ${val(n.salt_100g)} g</li>
				</ul>
			</div>
			<div class="dashboard-card">
				<h3>Sugar/Carb Risk</h3>
				<div style="margin-top:10px;">
					<div class="risk-bar" style="height:16px;border-radius:9999px;background:linear-gradient(90deg,#22c55e,#eab308,#ef4444);position:relative;">
						<div class="risk-pointer" style="position:absolute;top:-6px;width:2px;height:28px;background:#111827;left:0;"></div>
					</div>
					<div class="risk-label" style="margin-top:8px;font-weight:600;"></div>
				</div>
			</div>
			<div class="dashboard-card">
				<h3>AI Insights</h3>
				<ul>${(result.insights?.recommendations || []).map(t => `<li>${escapeHtml(t)}</li>`).join('')}</ul>
			</div>
		`;

		// Animate the rating meter to A–E position
		const fill = container.querySelector('.rating-fill');
		const widths = { 'A': '100%', 'B': '80%', 'C': '60%', 'D': '40%', 'E': '20%' };
		requestAnimationFrame(() => { if (fill) fill.style.width = widths[rating] || '0%'; });

		// Position risk pointer based on sugars and carbs
		const riskPointer = container.querySelector('.risk-pointer');
		const riskLabel = container.querySelector('.risk-label');
		const sugars = Number(n.sugars_100g || 0);
		const carbsVal = Number(n.carbohydrates_100g || 0);
		let risk = Math.min(100, Math.max(0, sugars * 3 + Math.max(0, carbsVal - sugars)));
		if (riskPointer) riskPointer.style.left = risk + '%';
		if (riskLabel) riskLabel.textContent = risk >= 66 ? 'High Risk Zone' : (risk >= 33 ? 'Medium Risk Zone' : 'Low Risk Zone');
	}

	function val(v) {
		return (v == null || Number.isNaN(v)) ? '-' : v;
	}

	function escapeHtml(s) {
		return String(s).replace(/[&<>]/g, function(c){ return ({'&':'&amp;','<':'&lt;','>':'&gt;'}[c]); });
	}

	// Prefer sessionStorage payload written by script.js; else support direct link with ?barcode=xxx
	const stored = sessionStorage.getItem('lastScan');
	if (stored) {
		try { render(JSON.parse(stored)); } catch { container.innerHTML = '<p>Failed to parse stored result.</p>'; }
		return;
	}

	const params = new URLSearchParams(location.search);
	const barcode = params.get('barcode');
	if (!barcode) {
		container.innerHTML = '<p>No scan data. Go back and scan a product.</p>';
		return;
	}

	fetch('/api/scan?barcode=' + encodeURIComponent(barcode))
		.then(r => r.ok ? r.json() : r.json().then(e => Promise.reject(e)))
		.then(render)
		.catch(e => {
			container.innerHTML = '<p>Error: ' + (e?.error || 'Unable to load result') + '</p>';
		});
});


