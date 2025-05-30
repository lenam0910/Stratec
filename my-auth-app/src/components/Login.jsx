import { useState } from 'react'

function Login({ setPage }) {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const handleSubmit = (e) => {
        e.preventDefault()
        console.log('Login:', { username, password })
        // Chuyển đến trang tải hình ảnh sau khi đăng nhập
        setPage('upload')
        // Thêm logic gửi dữ liệu đến backend nếu cần
    }

    return (
        <div className="form-container">
            <h2>Đăng Nhập</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Nhập username"
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Nhập password"
                    />
                </div>
                <button type="submit">Đăng Nhập</button>
                <p>Chưa có tài khoản? Đăng ký</p>
            </form>
        </div>
    )
}

export default Login