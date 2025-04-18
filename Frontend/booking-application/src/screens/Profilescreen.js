import React, { useEffect, useState } from 'react';
import { Tabs } from 'antd';
import axios, { isAxiosError } from 'axios';
import Loader from '../components/Loader';
import Swal from 'sweetalert2';
import { Tag } from 'antd';

const token = JSON.parse(localStorage.getItem('token'));
const userObj = JSON.parse(localStorage.getItem('user'));
const items = [
    {
        key: '1',
        label: 'Profile',
        children: <MyProfile />,
    },
    {
        key: '2',
        label: 'Bookings',
        children: <MyBookings />,
    }
];
function Profilescreen() {
    useEffect(() => {
        if (!token || !userObj)
            window.location.href = '/login'
    }, []);

    return (
        <div className='ml-3 mt-3'>
            <Tabs defaultActiveKey="1" items={items} />
        </div>
    )
}

export default Profilescreen;


export function MyBookings() {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const cancelBooking = async (reservationObj) => {
        try {
            setLoading(true);
            const resp = await axios.put(
                '/backend/reservation/' + reservationObj.reservationId,
                {},
                {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                }
            );
            setLoading(false);
            Swal.fire('Congratulations', 'Your booking has been cancelled successfully.', 'success').then(result => {
                window.location.reload()
            });
        } catch (error) {
            console.error('Error canceling reservation: ', error);
            setLoading(false);
            isAxiosError(error)
                ? setError(error.response?.data?.errorMessage)
                : setError('Unexpected error occured.');
            Swal.fire('Oops', 'Something went wrong.', 'error');
        }
    };
    useEffect(() => {
        const fetchReservation = async () => {
            try {
                setLoading(true);
                const reservationResp = await axios.get('/backend/reservation/' + userObj.id + '?limit=10&offset=0', {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                });
                setReservations(reservationResp.data.content || []);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching reservations: ', error);
                setLoading(false);
                isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
            }
        }
        fetchReservation();
    }, [])
    return (
        <div>
            <div className="row">
                <div className="col-md-6">
                    {loading && (<Loader />)}
                    {reservations && reservations.length > 0 ? ((reservations.map(reservation => {
                        return <div className='box-shadow'>
                            <h1>{reservation.roomName}</h1>
                            <br />
                            <p><b>Booking Id</b> : {reservation.reservationId}</p>
                            <p><b>Room </b> : {reservation.roomName}</p>
                            <p><b>Check In Date</b> : {reservation.checkInDate}</p>
                            <p><b>Check Out Date</b> : {reservation.checkOutDate}</p>
                            <p><b>Total Amount</b> : ${reservation.amount}</p>
                            <p><b>Payment Method</b> : {reservation.paymentMethod}</p>
                            <p><b>Status</b> : {reservation.status.toLowerCase().includes('cancel') ? <Tag color="orange">{reservation.status}</Tag> : <Tag color="green">{reservation.status}</Tag>}</p>
                            {reservation.status !== 'CANCELLED' && (<div className='text-right'>
                                <button class='btn btn-primary' onClick={() => { cancelBooking(reservation) }}>Cancel Booking</button>
                            </div>)}
                        </div>
                    }))) : (
                        <p>No booking found</p>
                    )}
                    {reservations && reservations.length === 0}
                </div>
            </div>
        </div>
    )
}

export function MyProfile() {
    return (
        <div>
            <h1>My Profile</h1>
            <br />
            <h1>Email: {userObj.email}</h1>
            <h1>First Name: {userObj.firstname}</h1>
            <h1>Last Name: {userObj.lastname}</h1>
        </div>
    )
}


