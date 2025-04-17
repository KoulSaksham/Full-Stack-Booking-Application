import React from 'react'

function Navbar() {
  const userObj = JSON.parse(localStorage.getItem('user'));
  function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('refresh')
    window.location.href = '/login'
  }
  return (
    <div>
      <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
          <a class="navbar-brand" href="/home">Booking App</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon" > <i class="fa-solid fa-bars" style={{ color: 'white' }}></i></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav">
              {
                userObj ? (<><div class="dropdown">
                  <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-user"></i>
                    {userObj.firstname}
                  </button>
                  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                    <a class="dropdown-item" href="/profile">Profile</a>
                    <a class="dropdown-item" href="#" onClick={logout}>Logout</a>

                  </div>
                </div></>) : (<>
                  <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/register">Register</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link active" href="/login">Login</a>
                  </li></>)
              }
            </ul>
            {/* <form class="d-flex" role="search">
              <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search" />
              <button class="btn btn-outline-success" type="submit">Search</button>
            </form> */}
          </div>
        </div>
      </nav>
    </div>
  )
}
export default Navbar