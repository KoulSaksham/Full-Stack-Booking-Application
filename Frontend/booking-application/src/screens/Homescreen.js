import axios, { isAxiosError } from 'axios';
import { React, useEffect, useState } from 'react';
import Room from '../components/Room';
import Loader from '../components/Loader';
import Error from '../components/Error';
import { DatePicker } from 'antd';
const { RangePicker } = DatePicker;

function Homescreen() {
  const token = JSON.parse(localStorage.getItem('token'));
  const userObj = JSON.parse(localStorage.getItem('user'));
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fromDate, setFromDate] = useState();
  const [toDate, setToDate] = useState();
  const [filters, setFilters] = useState([]);
  const [searchKey, setSearchKey] = useState('');
  const [limit, setLimit] = useState(7);
  const [offset, setOffset] = useState(0);
  const [totalRooms, setTotalRooms] = useState(0);
  const [hasSearched, setHasSearched] = useState(false);
  const [filterOptions, setFilterOptions] = useState({}); // { filterName: [values] }
  const [selectedFilters, setSelectedFilters] = useState({}); // { filterName: selectedValue }

  // Only fetch filters on mount
  useEffect(() => {
    if (!token) {
      window.location.href = '/login';
      return;
    } else if (token && userObj && userObj.isAdmin) {
      window.location.href = '/admin';
      return;
    } fetchFilters();
  }, []);

  const fetchFilters = async () => {
    try {
      setLoading(true);
      const filtersResp = await axios.get('/rooms/filters');

      setFilters(filtersResp.data.content);

      const options = {};
      for (const filterObj of filtersResp.data.content) {
        const filterName = filterObj.filter;
        try {
          const { data: values } = await axios.get(`/rooms/${filterName}`, {
            headers: { Authorization: 'Bearer ' + token }
          });
          options[filterName] = values;
        } catch (err) {
          console.error(`Error fetching values for ${filterName}:`, err);
        }
      }
      setFilterOptions(options);

    } catch (error) {
      isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleDynamicFilterChange = (filterKey, value) => {
    setSelectedFilters((prev) => ({
      ...prev,
      [filterKey]: value,
    }));
    setOffset(0);
  };

  const buildQueryParams = () => {
    const params = new URLSearchParams({
      limit: limit.toString(),
      offset: offset.toString(),
    });
    if (fromDate) params.append('checkInDate', fromDate);
    if (toDate) params.append('checkOutDate', toDate);
    if (searchKey) params.append('searchText', searchKey);
    // Add dynamic filters
    Object.entries(selectedFilters).forEach(([key, value]) => {
      if (value) params.append(key, value);
    });
    return params.toString();
  };

  const isDateRangeValid = () => fromDate && toDate;

  // Reactive fetchRoomsWithFilters when filters change
  useEffect(() => {
    if (isDateRangeValid()) {
      fetchRoomsWithFilters();
      setHasSearched(true);
    }
  }, [fromDate, toDate, searchKey, offset, limit, selectedFilters]);

  const fetchRoomsWithFilters = async () => {
    try {
      setLoading(true);
      const query = buildQueryParams();
      const { data } = await axios.get(`/rooms?${query}`, {
        headers: { Authorization: 'Bearer ' + token },
      });
      setRooms(data.content || []);
      setTotalRooms(data.totalElements || 0);
    } catch (error) {
      isAxiosError(error) ? setError(error.response?.data?.errorMessage) : setError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleDateChange = (dates) => {
    setFromDate(dates?.[0]?.format('YYYY-MM-DD'));
    setToDate(dates?.[1]?.format('YYYY-MM-DD'));
    setOffset(0); // reset page
  };

  const handleSearchInput = (e) => {
    setSearchKey(e.target.value);
    setOffset(0);
  };

  const handleNext = () => {
    if (offset + limit < totalRooms) {
      setOffset(offset + limit);
    }
  };

  const handlePrevious = () => {
    if (offset - limit >= 0) {
      setOffset(offset - limit);
    }
  };

  return (
    <div className='container' style={{ paddingBottom: '100px' }}>
      <div className="row mt-5 box-shadow">
        <div className="col-md-3">
          <RangePicker format='YYYY-MM-DD' onChange={handleDateChange} />
        </div>
        <div className="col-md-3">
          <input
            type="text"
            className="form-control"
            placeholder="Search Keyword"
            value={searchKey}
            onChange={handleSearchInput}
            onKeyUp={() => { }} // optional no-op, could be removed
          />
        </div>
        {filters.map((filterObj) => {
          const filterName = filterObj.filter;
          return (
            <div className="col-md-3 mt-2" key={filterName}>
              <select
                className="form-control"
                value={selectedFilters[filterName] || ''}
                onChange={(e) => handleDynamicFilterChange(filterName, e.target.value)}
              >
                <option value="">All {filterName}</option>
                {(filterOptions[filterName] || []).map((val) => (
                  <option key={val} value={val}>{val}</option>
                ))}
              </select>
            </div>
          );
        })}

      </div>

      {hasSearched && (
        <>
          <div className="row justify-content-center mt-5">
            {loading ? (
              <Loader />
            ) : error ? (
              <Error message={error} />
            ) : (
              rooms.map((room) => (
                <div className="col-md-9 mt-2" key={room.id}>
                  <Room room={room} fromDate={fromDate} toDate={toDate} />
                </div>
              ))
            )}
          </div>

          <div
            className="fixed-bottom bg-white border-top shadow-sm py-2"
            style={{ zIndex: 1050 }}
          >
            <div className="container d-flex justify-content-between align-items-center flex-wrap">

              {/* Previous Button */}
              <button
                className="btn btn-outline-primary btn-sm"
                onClick={handlePrevious}
                disabled={offset === 0}
              >
                &larr; Previous
              </button>

              {/* Page Number */}
              <span className="fw-bold">Page {Math.floor(offset / limit) + 1}</span>

              {/* Limit & Offset Controls */}
              <div className="d-flex align-items-center gap-3">
                {/* Limit */}
                <div className="d-flex align-items-center gap-1">
                  <label className="fw-semibold mb-0" style={{ fontSize: '0.9rem' }}>Limit:</label>
                  <select
                    value={limit}
                    onChange={(e) => setLimit(Number(e.target.value))}
                    style={{
                      height: '28px',
                      fontSize: '0.85rem',
                      padding: '2px 6px',
                      lineHeight: '1',
                      minWidth: '60px',
                    }}
                  >
                    {[5, 7, 10, 15, 20].map((val) => (
                      <option key={val} value={val}>{val}</option>
                    ))}
                  </select>
                </div>

                {/* Offset */}
                <div className="d-flex align-items-center gap-1">
                  <label className="fw-semibold mb-0" style={{ fontSize: '0.9rem' }}>Offset:</label>
                  <input
                    type="number"
                    value={offset}
                    onChange={(e) => setOffset(Number(e.target.value))}
                    style={{
                      height: '28px',
                      fontSize: '0.85rem',
                      padding: '2px 6px',
                      lineHeight: '1',
                      width: '70px',
                    }}
                  />
                </div>
              </div>

              {/* Next Button */}
              <button
                className="btn btn-outline-primary btn-sm"
                onClick={handleNext}
                disabled={offset + limit >= totalRooms}
              >
                Next &rarr;
              </button>
            </div>
          </div>


        </>
      )}
    </div>
  );
}
export default Homescreen;
