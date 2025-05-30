import { useState } from 'react'

function Register({ setPage }) {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const handleSubmit = (e) => {
        e.preventDefault()
        console.log('Register:', { username, password })
        // Thêm logic gửi dữ liệu đến backend nếu cần
    }

    return (
        <div className="form-container">
            <h2>Đăng Ký</h2>
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
                <button type="submit">Đăng Ký</button>
                <p>Đã có tài khoản?{' '}
                    <span
                        onClick={() => setPage('login')}
                        className="link"
                    >
                        Đăng nhập
                    </span>
                </p>
            </form>
        </div>
    )
}

export default Register