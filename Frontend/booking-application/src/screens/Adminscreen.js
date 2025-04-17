import React from 'react';
import { Tabs } from 'antd';
import { useEffect, useState } from 'react';
import axios, { isAxiosError } from 'axios';
import Loader from '../components/Loader';

const token = JSON.parse(localStorage.getItem('token'));
const userObj = JSON.parse(localStorage.getItem('user'));
const items = [
    {
        key: '1',
        label: 'Bookings',
        children: <Bookings />,
    },
    {
        key: '2',
        label: 'Users',
        children: <Users />,
    }
];
function Adminscreen() {
    useEffect(() => {
        if (!token || !userObj)
            window.location.href('/login')
        else if (token && userObj && !userObj.isAdmin)
            window.location.href('/home')
    }, []);

    return (
        <div className='ml-3 mt-3 mr-3 box-shadow'>
            <h2 className='text-center' style={{ fontSize: '30px' }}><b>Admin Panel</b></h2>
            <Tabs defaultActiveKey="1" items={items} />
        </div>
    )
}

export function Bookings() {
    const [bookings, setBookings] = useState([])
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchAllReservations = async () => {
            try {
                setLoading(true);
                const resp = await axios.get('/reservation?limit=10&offset=0', {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                });
                setBookings(resp.data.content || []);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching reservations :', error);
                setLoading(false);
                isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
            }
        };
        fetchAllReservations();
    }, [])

    return (
        <div className='row'>
            <div className="col-md-12">
                <h1>Bookings</h1>
                {loading && (<Loader />)}
                <table className='table table-bordered table-dark'>
                    <thead className='box-shadow thead-dark'>
                        <tr>
                            <th>
                                Booking Id
                            </th>
                            <th>
                                User Id
                            </th>
                            <th>
                                User Email
                            </th>
                            <th>
                                Room
                            </th>
                            <th>
                                From
                            </th>
                            <th>
                                To
                            </th>
                            <th>
                                Status
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {bookings.length && (bookings.map(elem => {
                            return <tr>
                                <td>
                                    {elem.reservationId ? elem.reservationId : '-'}
                                </td>
                                <td>
                                    {elem.userId ? elem.userId : '-'}
                                </td>
                                <td>
                                    {elem.userEmail ? elem.userEmail : '-'}
                                </td>
                                <td>
                                    {elem.roomName ? elem.roomName : '-'}
                                </td>
                                <td>
                                    {elem.checkInDate ? elem.checkInDate : '-'}
                                </td>
                                <td>
                                    {elem.checkOutDate ? elem.checkOutDate : '-'}
                                </td>
                                <td>
                                    {elem.status ? elem.status : '-'}
                                </td>
                            </tr>
                        }))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export function Users() {
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchAllReservations = async () => {
            try {
                setLoading(true);
                const resp = await axios.get('/user/all?limit=10&offset=0', {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                });
                setUsers(resp.data.content || []);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching users :', error);
                setLoading(false);
                isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
            }
        };
        fetchAllReservations();
    }, [])

    return (
        <div className='row'>
            <div className="col-md-12">
                <h1>Users</h1>
                {loading && (<Loader />)}
                <table className='table table-bordered table-dark'>
                    <thead className='box-shadow thead-dark'>
                        <tr>
                            <th>
                                Id
                            </th>
                            <th>
                                Username
                            </th>
                            <th>
                                Firstname
                            </th>
                            <th>
                                Lastname
                            </th>
                            <th>
                                Email
                            </th>
                            <th>
                                Created At
                            </th>
                            <th>
                                Deleted At
                            </th>

                        </tr>
                    </thead>
                    <tbody>
                        {users.length && (users.map(elem => {
                            return <tr>
                                <td>
                                    {elem.id}
                                </td>
                                <td>
                                    {elem.username}
                                </td>
                                <td>
                                    {elem.firstname}
                                </td>
                                <td>
                                    {elem.lastname ? elem.lastname : '-'}
                                </td>
                                <td>
                                    {elem.email}
                                </td>
                                <td>
                                    {elem.createdAt}
                                </td>
                                <td>
                                    {elem.deletedAt ? elem.deletedAt : '-'}
                                </td>
                            </tr>
                        }))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default Adminscreen;