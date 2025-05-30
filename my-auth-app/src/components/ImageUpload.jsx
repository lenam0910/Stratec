import { useState } from 'react';
import axios from 'axios'; // Thêm import axios

function ImageUpload({ setPage }) {
    const [image, setImage] = useState(null);
    const [imageName, setImageName] = useState('');
    const [uploadStatus, setUploadStatus] = useState('');

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setImage({ file, url: imageUrl });
            console.log('Selected image:', file);
        }
    };

    const handleImageNameChange = (e) => {
        setImageName(e.target.value);
    };

    const handleUpload = async () => {
        if (!image || !imageName) {
            setUploadStatus('Vui lòng chọn hình ảnh và nhập tên hình ảnh.');
            return;
        }

        const formData = new FormData();
        formData.append('image', image.file);
        formData.append('imageName', imageName);

        try {
            console.log('Sending request to: http://localhost:8080/upload-image');
            const response = await axios.post('http://localhost:8080/upload-image', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true, // Gửi cookie
            });
            console.log('Response status:', response.status);
            console.log('Response data:', response.data);
            if (response.status === 200 && response.data.code === 1000) {
                setUploadStatus('Tải lên thành công!');
            } else {
                setUploadStatus(`Lỗi: ${response.data.message || 'Tải lên thất bại.'}`);
            }
        } catch (error) {
            console.error('Error details:', error);
            setUploadStatus('Lỗi: Không thể kết nối đến server.');
        }
    };

    return (
        <div className="form-container">
            <h2>Tải Lên Hình Ảnh</h2>
            <div className="form-group">
                <label>Chọn hình ảnh</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleImageChange}
                />
            </div>
            <div className="form-group">
                <label>Tên hình ảnh</label>
                <input
                    type="text"
                    value={imageName}
                    onChange={handleImageNameChange}
                    placeholder="Nhập tên hình ảnh"
                />
            </div>
            {image && (
                <div className="preview">
                    <p>Xem trước:</p>
                    <img src={image.url} alt="Preview" style={{ maxWidth: '300px' }} />
                </div>
            )}
            <button onClick={handleUpload} className="upload-button">
                Tải Lên
            </button>
            {uploadStatus && (
                <p className={uploadStatus.includes('thành công') ? 'success' : 'error'}>
                    {uploadStatus}
                </p>
            )}
            <button
                onClick={() => setPage('login')}
                className="back-button"
            >
                Quay Lại Đăng Nhập
            </button>
        </div>
    );
}

export default ImageUpload;