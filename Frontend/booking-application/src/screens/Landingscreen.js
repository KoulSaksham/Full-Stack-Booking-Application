import React from 'react';
import { useNavigate } from 'react-router-dom';

function LandingScreen() {
    const navigate = useNavigate();

    return (
        <div
            className="vh-100 vw-100 position-relative"
            style={{
                backgroundImage: "url('http://localhost:8082/backend/cover_photo.jpg')",
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat'
            }}
        >
            <div className="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center text-center text-white" style={{ backgroundColor: 'rgba(0,0,0,0.6)', padding: '0 1rem' }}>
                <h1 className="display-3 fw-bold mb-4">Booking Application</h1>
                <p className="fs-4 fst-italic mb-4">
                    Wherever you go, go with all your heart — and let me handle the rest.
                </p>
                <button
                    className="btn btn-light btn-lg px-4 py-2 fw-semibold shadow"
                    onClick={() => navigate('/home')}
                >
                    Get Started
                </button>
            </div>
        </div>
    );
}

export default LandingScreen;
