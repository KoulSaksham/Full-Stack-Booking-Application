import React from 'react';
import { Link } from 'react-router-dom';


function Landingscreen() {
    return (
        <div className='row landing justify-content-center'>
            <div className="col-md-9 my-auto text-center" style={{borderRight:'8px solid white'}}>
                <h2 style={{ color: 'white', fontSize: '120px' }}>
                    Booking Application
                </h2>
                <h1 style={{ color: 'white', paddingTop:'30px' }}>
                    Some Quotation
                </h1>
                <Link to='/home'>
                    <button className='btn landing-btn' style={{ marginTop:'30px' }}>
                        Get Started
                    </button>
                </Link>
            </div>
        </div>
    )
}

export default Landingscreen