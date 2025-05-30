// src/components/ImageDisplay.js
import React, { useState } from 'react';
import axios from 'axios';

const ImageDisplay = ({ setPage }) => {
    const [imageId, setImageId] = useState(''); // Lưu giá trị imageId người dùng nhập
    const [imageData, setImageData] = useState(''); // Lưu dữ liệu base64 của ảnh
    const [error, setError] = useState(''); // Lưu thông báo lỗi
    const [loading, setLoading] = useState(false); // Trạng thái tải

    // Hàm gọi API để lấy ảnh
    const fetchImage = async () => {
        if (!imageId) {
            setError('Vui lòng nhập ID ảnh');
            return;
        }
        setLoading(true);
        setError('');
        try {
            const response = await axios.get(`http://localhost:8080/${imageId}`);
            const data = response.data;
            if (data.code === 1000 && data.data.imageData) {
                // Chuyển dữ liệu base64 thành định dạng hiển thị được
                setImageData(`data:image/jpeg;base64,${data.data.imageData}`);
            } else {
                setError(data.message || 'Không thể lấy ảnh');
            }
        } catch (err) {
            setError('Lỗi khi kết nối đến server: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    // Xử lý khi nhấn Enter
    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            fetchImage();
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-lg">
                <h2 className="text-2xl font-bold text-gray-800 mb-4 text-center">
                    Hiển Thị Ảnh Từ MinIO
                </h2>

                {/* Input để nhập imageId */}
                <div className="mb-4">
                    <input
                        type="text"
                        value={imageId}
                        onChange={(e) => setImageId(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="Nhập ID ảnh (ví dụ: tên-file-gốc_123456789)"
                        className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                {/* Nút lấy ảnh */}
                <button
                    onClick={fetchImage}
                    disabled={loading}
                    className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition disabled:bg-gray-400"
                >
                    {loading ? 'Đang tải...' : 'Lấy Ảnh'}
                </button>

                {/* Nút quay lại trang login */}
                <button
                    onClick={() => setPage('login')}
                    className="w-full mt-2 bg-gray-500 text-white p-2 rounded hover:bg-gray-600 transition"
                >
                    Quay Lại Đăng Nhập
                </button>

                {/* Hiển thị lỗi nếu có */}
                {error && (
                    <p className="text-red-500 mt-2 text-center">{error}</p>
                )}

                {/* Hiển thị ảnh nếu có dữ liệu */}
                {imageData && (
                    <div className="mt-4">
                        <img
                            src={imageData}
                            alt="Ảnh từ MinIO"
                            className="w-full max-w-md mx-auto rounded-lg shadow-sm"
                        />
                    </div>
                )}
            </div>
        </div>
    );
};

export default ImageDisplay;