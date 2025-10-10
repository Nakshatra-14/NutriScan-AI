from flask import Flask, jsonify, request, send_from_directory
import pandas as pd
import numpy as np
import os # Import the 'os' module

app = Flask(__name__)

# --- 1. SETUP STATIC FILE SERVING ---
# Get the absolute path to the directory where the script is running.
# This makes sure we can find the 'assets' folder reliably.
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
ASSETS_DIR = os.path.join(BASE_DIR, 'assets')

# Load the dataset
try:
    csv_path = os.path.join(BASE_DIR, 'food_data.csv')
    food_data = pd.read_csv(csv_path)
    # Convert barcode column to string to ensure matching
    food_data['code'] = food_data['code'].astype(str)
except FileNotFoundError:
    print(f"Error: 'food_data.csv' not found at {csv_path}. Make sure the file is in the same directory as the script.")
    food_data = None

# --- This new route will handle requests for images ---
@app.route('/assets/<path:filename>')
def serve_asset(filename):
    """Serves a file from the 'assets' directory."""
    return send_from_directory(ASSETS_DIR, filename)


@app.route('/api/v2/product/<string:barcode>', methods=['GET'])
def get_product_details(barcode):
    if food_data is None:
        return jsonify({"status": 404, "message": "Database not found."}), 404

    product_info = food_data[food_data['code'] == barcode]

    if product_info.empty:
        return jsonify({"status": 404, "product": None, "message": "Product not found"}), 404

    product = product_info.iloc[0]

    def convert_types(value):
        if pd.isna(value):
            return None
        if isinstance(value, (np.int64, np.int32)):
            return int(value)
        if isinstance(value, (np.float64, np.float32)):
            return float(value)
        return value

    # --- 2. CONSTRUCT THE FULL IMAGE URL ---
    image_filename = product.get('image_filename')
    full_image_url = None
    if pd.notna(image_filename):
        # Clean up the filename: replace backslashes with forward slashes
        clean_filename = image_filename.replace('\\', '/').split('/')[-1]
        # Build the full URL using the server's address
        # request.host_url will be like "http://192.168.29.235:8000/"
        full_image_url = f"{request.host_url}assets/{clean_filename}"

    response_data = {
        "status": 1,
        "code": barcode,
        "product": {
            "product_name": product.get('product_name'),
            "quantity": str(product.get('quantity')),
            "brands": product.get('Brand'),
            "nova_group": convert_types(product.get('nova_group')),
            # ... other fields
            "selected_images": {
                "front": {
                    "display": {
                        # --- Use the full URL here ---
                        "en": full_image_url
                    }
                }
            },
            "nutriments": {
                "sugars_100g": convert_types(product.get('sugars_100g')),
                "carbohydrates_100g": convert_types(product.get('carbohydrates_100g')),
                "fat_100g": convert_types(product.get('fat_100g')),
                "salt_100g": convert_types(product.get('salt_100g')),
                "energy-kcal_100g": convert_types(product.get('energy-kcal_100g')),
                "saturated-fat_100g": convert_types(product.get('saturated-fat_100g')),
                "proteins_100g": convert_types(product.get('proteins_100g')),
                "fiber_100g": convert_types(product.get('fiber_100g'))
            },
            "nutrient_levels": {
                "fat": product.get('fat_level'),
                "salt": product.get('salt_level'),
                "sugars": product.get('sugars_level'),
                "saturated-fat": product.get('saturated-fat_level')
            },
             # Add missing fields and set to null if not in CSV
            "nutrition_grades": None,
            "ecoscore_grade": None,
            "allergens_from_ingredients": None
        }
    }
    return jsonify(response_data)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)