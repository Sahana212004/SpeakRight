# app.py
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from werkzeug.security import generate_password_hash, check_password_hash
import os

app = Flask(__name__)
CORS(app)

BASE_DIR = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(BASE_DIR, "users.db")
app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{db_path}"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db = SQLAlchemy(app)

# -----------------------
# Models
# -----------------------
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=True)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    phone = db.Column(db.String(32), nullable=True)

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name or "",
            "email": self.email,
            "phone": self.phone or ""
        }

# Create DB
with app.app_context():
    db.create_all()

# -----------------------
# Helper
# -----------------------
def json_error(message, code=400):
    return jsonify({"success": False, "message": message}), code

# -----------------------
# Routes
# -----------------------

@app.route("/signup", methods=["POST"])
def signup():
    """
    Expects JSON:
    {
      "name": "Sahana",
      "email": "s@example.com",
      "password": "secret",
      "phone": "999..."
    }
    """
    data = request.get_json(force=True, silent=True)
    if not data:
        return json_error("Missing JSON body", 400)

    email = data.get("email")
    password = data.get("password")
    name = data.get("name")
    phone = data.get("phone")

    if not email or not password:
        return json_error("email and password required", 400)

    if User.query.filter_by(email=email).first():
        return json_error("User with this email already exists", 409)

    user = User(
        name=name,
        email=email,
        password_hash=generate_password_hash(password),
        phone=phone
    )
    db.session.add(user)
    db.session.commit()

    return jsonify({"success": True, "message": "User created", "user": user.to_dict()}), 201

@app.route("/login", methods=["POST"])
def login():
    """
    Expects JSON:
    {
      "email": "s@example.com",
      "password": "secret"
    }

    Returns user data if ok.
    """
    data = request.get_json(force=True, silent=True)
    if not data:
        return json_error("Missing JSON body", 400)

    email = data.get("email")
    password = data.get("password")
    if not email or not password:
        return json_error("email and password required", 400)

    user = User.query.filter_by(email=email).first()
    if not user or not check_password_hash(user.password_hash, password):
        return json_error("Invalid credentials", 401)

    return jsonify({"success": True, "message": "Login successful", "user": user.to_dict()}), 200

@app.route("/user/<int:user_id>", methods=["GET"])
def get_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return json_error("User not found", 404)
    return jsonify({"success": True, "user": user.to_dict()}), 200

@app.route("/profile/<int:user_id>", methods=["PUT"])
def update_profile(user_id):
    """
    Expects JSON with fields to update, e.g.
    {
      "name": "New Name",
      "phone": "123"
    }
    """
    data = request.get_json(force=True, silent=True)
    if not data:
        return json_error("Missing JSON body", 400)

    user = User.query.get(user_id)
    if not user:
        return json_error("User not found", 404)

    # Only allow safe fields
    if "name" in data:
        user.name = data.get("name")
    if "phone" in data:
        user.phone = data.get("phone")
    if "password" in data and data.get("password"):
        user.password_hash = generate_password_hash(data.get("password"))

    db.session.commit()
    return jsonify({"success": True, "message": "Profile updated", "user": user.to_dict()}), 200

# Health check
@app.route("/ping", methods=["GET"])
def ping():
    return jsonify({"success": True, "message": "pong"}), 200

# -----------------------
# Run
# -----------------------
if __name__ == "__main__":
    # host 0.0.0.0 so Android devices/emulators can reach the dev server
    app.run(host="0.0.0.0", port=5000, debug=True)
