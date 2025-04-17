import React, { useState } from 'react'
import { Modal, Button, Carousel } from 'react-bootstrap';
import { Link } from 'react-router-dom';

function Room({ room, fromDate, toDate }) {
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);
    return (
        <div className='row box-shadow'>
            <div className="col-md-4">
                <img src={room.imageUrl} className='small-img' />
            </div>
            <div className="col-md-7 text-left">
                <h1>{room.name}</h1>
                {" "}
                <p>Occupancy : {room.occupancy}</p>
                <p>Room Type : {room.roomType}</p>
                <p>Location : {room.location}</p>
                <div style={{ float: 'right' }}>
                    {(fromDate && toDate) && (
                        <Link to={`/book/${room.id}/${fromDate}/${toDate}`}>
                            <button className='btn btn-primary m-2'>Book Now</button>
                        </Link>
                    )}

                    <button className='btn btn-primary' onClick={handleShow}>View Details</button>
                </div>
            </div>

            <Modal show={show} onHide={handleClose} size='lg'>
                <Modal.Header>
                    <Modal.Title>{room.name}</Modal.Title>
                </Modal.Header>
                <Modal.Body>

                    <Carousel>
                        <Carousel.Item>
                            <img src={room.imageUrl} className='d-block w-100 big-img' />
                        </Carousel.Item>

                    </Carousel>
                    <p>{room.description}</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>

                </Modal.Footer>
            </Modal>
        </div>

    )
}

export default Room