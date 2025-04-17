import React, { useState } from 'react';
import { DotLoader } from 'react-spinners'; // ✅ Named import

function Loader() {
    const [loading, setLoading] = useState(true);
    const [color, setColor] = useState("#000000");

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',            // Full vertical height
            width: '100%',              // Full width
        }}>
            <div className="sweet-loading text-center">
                <DotLoader
                    color={color}
                    loading={loading}
                    size={180}
                    aria-label="Loading Spinner"
                    data-testid="loader"
                />
            </div>
        </div>

    );
}

export default Loader;