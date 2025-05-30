import { useState } from 'react';

function Register({ setPage }) {
    const [userName, setuserName] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Dữ liệu gửi đến backend
        const userData = {
            userName,
            password,
        };

        try {
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });

            const result = await response.json();

            if (response.ok && result.code === 1000) {
                setSuccess(result.message || 'Đăng ký thành công!');
                // Tùy chọn: Chuyển hướng sang trang đăng nhập sau khi đăng ký thành công
                setTimeout(() => setPage('login'), 2000);
            } else {
                setError(result.message || 'Đăng ký thất bại. Vui lòng thử lại.');
            }
        } catch (err) {
            setError('Có lỗi xảy ra. Vui lòng kiểm tra kết nối và thử lại.');
            console.error('Error:', err);
        }
    };

    return (
        <div className="form-container">
            <h2>Đăng Ký</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>{success}</p>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>userName</label>
                    <input
                        type="text"
                        value={userName}
                        onChange={(e) => setuserName(e.target.value)}
                        placeholder="Nhập userName"
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Nhập password"
                        required
                    />
                </div>
                <button type="submit">Đăng Ký</button>
                <p>
                    Đã có tài khoản?{' '}
                    <span
                        onClick={() => setPage('login')}
                        className="link"
                        style={{ cursor: 'pointer', color: 'blue' }}
                    >
                        Đăng nhập
                    </span>
                </p>
            </form>
        </div>
    );
}

export default Register;