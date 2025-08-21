import pandas as pd
import numpy as np
import random

# Number of data points to generate
num_samples = 2000

# Age groups
age_groups = ["10-15", "15-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50"]

data = []

for _ in range(num_samples):
    age_group = random.choice(age_groups)
    
    # Base nutritional values for a "balanced" meal
    protein = np.random.normal(50, 15)
    carb = np.random.normal(280, 50)
    fat = np.random.normal(75, 20)
    sugar = np.random.normal(25, 10)
    fiber = np.random.normal(25, 8)
    
    # Introduce some "unhealthy" meals
    if random.random() < 0.3: # 30% chance of being unhealthy
        label = "bad"
        fat += random.uniform(20, 50)
        sugar += random.uniform(20, 40)
        fiber -= random.uniform(5, 15)
        protein -= random.uniform(10, 30)
    else:
        label = "ok"

    # Ensure no negative values
    protein = max(0, protein)
    carb = max(0, carb)
    fat = max(0, fat)
    sugar = max(0, sugar)
    fiber = max(0, fiber)

    # --- Generate Synthetic Scores ---
    # Simplified Glycemic Index (lower is better)
    # Higher sugar and carbs might lead to a higher GI
    glycemic_index = np.random.normal(55, 15) + (sugar / 10) + (carb / 50)
    glycemic_index = min(100, max(0, glycemic_index))
    
    # Simplified Eco-Score (lower is better)
    # We can simulate that high protein might mean more meat, so a worse eco-score (higher value)
    eco_score = np.random.normal(50, 20) + (protein / 5)
    eco_score = min(100, max(0, eco_score))
    
    data.append({
        "protein": round(protein, 2),
        "carb": round(carb, 2),
        "fat": round(fat, 2),
        "sugar": round(sugar, 2),
        "fiber": round(fiber, 2),
        "age_group": age_group,
        "label": label,
        "glycemic_index": round(glycemic_index, 2),
        "eco_score": round(eco_score, 2)
    })

df = pd.DataFrame(data)
df.to_csv("food_dataset_v2.csv", index=False)

print("New dataset 'food_dataset_v2.csv' created successfully with 2000 samples.")