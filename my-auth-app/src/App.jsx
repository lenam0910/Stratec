import { useState } from 'react'
import Login from './components/Login'
import Register from './components/Register'
import ImageUpload from './components/ImageUpload'
import ImageList from './components/ImageList'; // Thêm dòng này

function App() {
  const [page, setPage] = useState('login')
  console.log('Current page:', page); // Thêm dòng này
  return (
    <div className="app-container">
      {page === 'login' && <Login setPage={setPage} />}
      {page === 'register' && <Register setPage={setPage} />}
      {page === 'upload' && <ImageUpload setPage={setPage} />}
      {page === 'imageList' && <ImageList setPage={setPage} />}
    </div>
  )
}
export default App
