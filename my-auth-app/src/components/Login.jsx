import { useState } from 'react';
import axios from 'axios';

function Login({ setPage }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(''); // Để hiển thị lỗi nếu đăng nhập thất bại

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Gửi yêu cầu POST đến endpoint /auth/login-cookie của backend
            const response = await axios.post(
                'http://localhost:8888/auth/login-cookie',
                {
                    userName: username,
                    password: password,
                },
                {
                    withCredentials: true, // Quan trọng: Cho phép gửi và nhận cookie
                }
            );

            // Nếu đăng nhập thành công
            console.log('Đăng nhập thành công:', response.data);
            setPage('upload');
        } catch (err) {
            // Xử lý lỗi
            console.error('Lỗi đăng nhập:', err);
            setError('Đăng nhập thất bại. Vui lòng kiểm tra username hoặc password.');
        }
    };

    return (
        <div className="form-container">
            <h2>Đăng Nhập</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
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
                <p>
                    Chưa có tài khoản?{' '}
                    <span style={{ color: 'blue', cursor: 'pointer' }} onClick={() => setPage('register')}>
                        Đăng ký
                    </span>
                </p>
            </form>
        </div>
    );
}

export default Login;