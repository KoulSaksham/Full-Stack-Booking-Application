import React from 'react';

function Error({ message }) {
    return (
        <div className="alert alert-danger text-center">
            {message || "Something went wrong! Pls try again later."}
        </div>
    );
}

export default Error;
