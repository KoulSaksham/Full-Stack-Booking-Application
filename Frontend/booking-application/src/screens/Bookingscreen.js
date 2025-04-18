import axios, { isAxiosError } from 'axios';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Loader from '../components/Loader';
import Error from '../components/Error';
import moment from 'moment';
import PaymentPopup from '../components/Paymentpopup';
import Swal from 'sweetalert2';

function BookingScreen() {
    const token = JSON.parse(localStorage.getItem('token'));
    const userObj = JSON.parse(localStorage.getItem('user'));
    const { roomId, fromDate, toDate } = useParams();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [room, setRoom] = useState();
    const [totalAmount, setTotalAmount] = useState();
    const [allowedPayments, setAllowedPayments] = useState([]);
    const [showPaymentPopup, setShowPaymentPopup] = useState(false);
    const [selectedPayment, setSelectedPayment] = useState(null);

    const totalDays = moment(toDate).diff(moment(fromDate), 'days') + 1;


    useEffect(() => {
        if (!userObj || !token)
            window.location.reload = '/login'

        const fetchParticularRoom = async () => {
            try {
                setLoading(true);
                const resp = await axios.get('/backend/rooms/' + roomId, {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                });
                const allowedPaymentsResp = await axios.get('/backend/payment/types', {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                });
                setAllowedPayments(allowedPaymentsResp.data);
                setTotalAmount(totalDays * resp?.data?.price)
                setRoom(resp.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching room:', error);
                setLoading(false);
                isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
            }
        }
        fetchParticularRoom();
    }, []);

    async function bookReservation() {
        if (!selectedPayment) {
            alert("No payment method selected.");
            return;
        }
        const reservationDetails = {
            roomId: room?.id,
            userId: userObj?.id,
            checkInDate: fromDate,
            checkOutDate: toDate,
            amount: totalAmount,
            paymentMethod: selectedPayment
        };
        try {
            setLoading(true);
            const resp = await axios.post('/backend/reservation', reservationDetails, {
                headers: {
                    Authorization: 'Bearer ' + token
                }
            });
            setLoading(false);
            Swal.fire('Congratulations', 'Your room has been booked successfully!', 'success').then(result => {
                window.location.href = '/profile'
            });
        } catch (error) {
            setLoading(false);
            Swal.fire('Oops', 'Something went wrong!', 'error');
        }

    }
    return (
        <div className='m-5'>
            {
                loading ? (<Loader />) : error ? (<Error message={error} />) :
                    (<div>
                        <div className="row justify-content-center mt-5 box-shadow">
                            <div className="col-md-5">
                                <h1>{room.name}</h1>
                                <img src={room.imageUrl} className='big-img' />
                            </div>
                            <div className="col-md-5">
                                <div style={{ textAlign: 'right' }}>
                                    <h1>Booking Details</h1>
                                    <hr />
                                    <b>
                                        <p>Name: {userObj?.firstname + ' ' + userObj?.lastname}</p>
                                        <p>From Date: {fromDate}</p>
                                        <p>To Date: {toDate}</p>
                                        <p>Occupancy: {room.occupancy}</p>
                                    </b>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                    <b>
                                        <h1>Amount</h1>
                                        <hr />
                                        <p>Total days: {totalDays}</p>
                                        <p>Per Day Cost: ${room.price}</p>
                                        <p>Total Amount: ${totalAmount}</p>
                                    </b>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                    <button className='btn btn-primary' onClick={() => setShowPaymentPopup(true)}>
                                        Pay Now
                                    </button>
                                </div>
                                <PaymentPopup
                                    visible={showPaymentPopup}
                                    onClose={() => setShowPaymentPopup(false)}
                                    onPay={(method) => {
                                        setSelectedPayment(method);
                                        bookReservation();  // called only after method is selected
                                    }}
                                    allowedPayments={allowedPayments}
                                />
                            </div>
                        </div>
                    </div>)
            }
        </div>
    )
}

export default BookingScreen