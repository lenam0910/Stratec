import { useState, useEffect } from 'react';
import axios from 'axios';
import '../ImageList.css';
function ImageList({ setPage }) {
    const [images, setImages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchImages = async () => {
            setLoading(true);
            setError('');
            try {
                const response = await axios.get('http://localhost:8888/img', {
                    withCredentials: true,
                });
                const data = response.data;
                if (data.code === 1000 && data.data) {
                    setImages(data.data);
                } else {
                    setError(data.message || 'Không thể lấy danh sách ảnh.');
                }
            } catch (err) {
                console.error('Error details:', err);
                if (err.response && err.response.status === 403) {
                    setError('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
                    setTimeout(() => setPage('login'), 2000);
                } else {
                    setError('Lỗi: Không thể kết nối đến server.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchImages();
    }, [setPage]);

    return (
        <div className="container">
            <div className="card">
                <h2 className="title">Danh Sách Hình Ảnh</h2>

                {loading && (
                    <div className="loading">
                        <div className="spinner"></div>
                        <p>Đang tải danh sách ảnh...</p>
                    </div>
                )}

                {error && (
                    <p className="error">{error}</p>
                )}

                {!loading && images.length === 0 && !error && (
                    <p className="no-images">Không có hình ảnh nào.</p>
                )}

                {!loading && images.length > 0 && (
                    <div className="grid">
                        {images.map((image) => (
                            <div key={image.id} className="image-card">
                                <div className="image-wrapper">
                                    <img
                                        src={`data:image/jpeg;base64,${image.imageData}`}
                                        alt={image.imageName}
                                        className="image"
                                    />
                                    <div className="overlay">
                                        <p className="overlay-text">{image.imageName}</p>
                                    </div>
                                </div>
                                <div className="image-info">
                                    <p className="image-name">Tên: {image.imageName}</p>
                                    <p className="image-id">ID: {image.id}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                <div className="button-group">
                    <button
                        onClick={() => setPage('upload')}
                        className="btn btn-primary"
                    >
                        Quay Lại Tải Ảnh
                    </button>
                    <button
                        onClick={() => setPage('login')}
                        className="btn btn-secondary"
                    >
                        Quay Lại Đăng Nhập
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ImageList;