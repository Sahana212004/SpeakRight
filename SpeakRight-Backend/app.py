# app.py
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from models import db, User
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# Database config
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///C:/Users/Dell/SpeakRight-Backend/users.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db.init_app(app)

with app.app_context():
    db.create_all()

# ------------------------------
# Signup Route
# ------------------------------
@app.route('/signup', methods=['POST'])
def signup():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    if User.query.filter_by(email=email).first():
        return jsonify({'message': 'User already exists!'}), 400

    new_user = User(email=email)
    new_user.set_password(password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({'message': 'User registered successfully!'}), 201

# ------------------------------
# Login Route
# ------------------------------

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    user = User.query.filter_by(email=email).first()
    if not user or not user.check_password(password):
        return jsonify({'message': 'Invalid credentials'}), 401

    # Determine if user has filled profile
    if not user.first_name or not user.last_name or not user.phone:
        first_time = True
    else:
        first_time = False

    return jsonify({
        'message': 'Login successful',
        'email': user.email,
        'first_name': user.first_name,
        'last_name': user.last_name,
        'phone': user.phone,
        'first_time': first_time
    }), 200

# ------------------------------
# Update Profile
# ------------------------------
@app.route('/update_profile', methods=['POST'])
def update_profile():
    data = request.get_json()
    email = data.get('email')
    first_name = data.get('first_name')
    last_name = data.get('last_name')
    phone = data.get('phone')

    user = User.query.filter_by(email=email).first()
    if not user:
        return jsonify({'message': 'User not found!'}), 404

    user.first_name = first_name
    user.last_name = last_name
    user.phone = phone
    db.session.commit()

    return jsonify({'message': 'Profile updated successfully!'}), 200

# ------------------------------
# Get User Details
# ------------------------------
@app.route('/get_user', methods=['GET'])
def get_user():
    email = request.args.get('email')
    user = User.query.filter_by(email=email).first()
    if not user:
        return jsonify({'message': 'User not found!'}), 404

    return jsonify({
        'email': user.email,
        'first_name': user.first_name,
        'last_name': user.last_name,
        'phone': user.phone
    }), 200

if __name__ == '__main__':
    print(app.url_map)
    app.run(host='0.0.0.0', port=5000)
