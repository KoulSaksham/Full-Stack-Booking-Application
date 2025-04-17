import React from 'react';

function Success({ message }) {
    return (
        <div className="alert alert-success text-center">
            {message || 'Operation Successful!'}
        </div>
    );
}

export default Success;