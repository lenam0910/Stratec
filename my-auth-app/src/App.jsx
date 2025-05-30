import { useState } from 'react'
import Login from './components/Login'
import Register from './components/Register'
import ImageUpload from './components/ImageUpload'

function App() {
  const [page, setPage] = useState('login')

  return (
    <div className="app-container">
      {page === 'login' && <Login setPage={setPage} />}
      {page === 'register' && <Register setPage={setPage} />}
      {page === 'upload' && <ImageUpload setPage={setPage} />}

    </div>
  )
}
export default App
