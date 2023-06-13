import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

interface Booking {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
}

interface BookingRequest {
  name: string;
  startDate: string;
  endDate: string;
}

const Booking: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [name, setName] = useState('');
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [selectedBookId, setSelectedBookingId] = useState('');


  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const response = await fetch('/bookings');
      const data = await response.json();
      setBookings(data);
    } catch (error) {
      console.error('Error fetching bookings:', error);
    }
  };

  const createBooking = async () => {
    if (!name || !startDate || !endDate) {
      setErrorMessage('Please fill in all fields.');
      return;
    }
    const newBooking: BookingRequest = {
      name: name,
      startDate: startDate?.toISOString().split('T')[0] || '',
      endDate: endDate?.toISOString().split('T')[0] || ''
    };

    try {
      const response = await fetch('/booking', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newBooking)
      });

      if (response.ok) {
        fetchBookings();
        setName('');
        setStartDate(null);
        setEndDate(null);
        setErrorMessage('');
      } else {
        console.error('Error creating booking:');
        setErrorMessage('Failed to create booking.');
      }
    } catch (error) {
      console.error('Error creating booking:', error);
      setErrorMessage('Failed to create booking.');
    }
  };

  const deleteBooking = async (id: string) => {
    try {
      const response = await fetch(`/booking/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        fetchBookings();
      } else {
        throw new Error('Failed to delete booking.');
      }
    } catch (error) {
      console.error('Error deleteling booking:', error);
    }
  };

  const updateBooking = async (id: string) => {
    if (!name || !startDate || !endDate) {
      setErrorMessage('Please fill in all fields.');
      return;
    }
    const updatedBooking: BookingRequest = {
      name: name,
      startDate: startDate?.toISOString().split('T')[0] || '',
      endDate: endDate?.toISOString().split('T')[0] || ''
    };
    try {
      const response = await fetch(`/booking/${id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedBooking)
      });

      if (response.ok) {
        fetchBookings();
        setErrorMessage('');
      } else {
        throw new Error('Failed to update booking.');
      }
    } catch (error) {
      console.error('Error updating booking:', error);
      setErrorMessage('Failed to update booking.');
    }
    setSelectedBookingId('');
  };

  const handleUpdateClick = (booking: Booking) => {
    setName(booking.name);
    setStartDate(new Date(booking.startDate));
    setEndDate(new Date(booking.endDate));
    setSelectedBookingId(booking.id);
  };


  return (
    <div>
      <h2>Create Booking</h2>
      <div>
        <label>Name: </label>
        <input
          type="text"
          name="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>
      <div className=''>
        <label>Start Date:</label>
        <DatePicker
          selected={startDate}
          onChange={(date) => setStartDate(date as Date)}
          dateFormat="yyyy-MM-dd"
          minDate={new Date()}
        />
      </div>
      <div>
        <label>End Date:</label>
        <DatePicker
          selected={endDate}
          onChange={(date) => setEndDate(date as Date)}
          dateFormat="yyyy-MM-dd"
          minDate={startDate ? new Date(startDate.getTime() + 1000*60*60*24) : new Date()}
        />
      </div>
      <button onClick={createBooking}>Create Booking</button>
      <h2>All Bookings</h2>
      {bookings.map((booking) => (
        <div key={booking.id}>
          <p>
            {booking.name}: {booking.startDate} to {booking.endDate}
          </p>
          <button onClick={() => deleteBooking(booking.id)}>delete</button>
          <button onClick={() => handleUpdateClick(booking)}>Update</button>
        </div>
      ))}

      {selectedBookId && (
        <div>
          <h2>Update Booking</h2>
          <div>
            <label>Name:</label>
            <input
              type="text"
              name="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div>
            <label>Start Date:</label>
            <DatePicker
              selected={startDate}
              onChange={(date) => setStartDate(date as Date)}
              dateFormat="yyyy-MM-dd"
              minDate={new Date()}
            />
          </div>
          <div>
            <label>End Date:</label>
            <DatePicker
              selected={endDate}
              onChange={(date) => setEndDate(date as Date)}
              dateFormat="yyyy-MM-dd"
              minDate={startDate ? new Date(startDate.getTime() + 1000*60*60*24) : new Date()}
            />
          </div>
          <button onClick={() => updateBooking(selectedBookId)}>Save</button>
        </div>
      )}

      {errorMessage && <p className="error-message">{errorMessage}</p>}

    </div>
  );
};

export default Booking;
