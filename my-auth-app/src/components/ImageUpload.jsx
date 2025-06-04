import { useState } from 'react';
import axios from 'axios';

function ImageUpload({ setPage }) {
    const [image, setImage] = useState(null);
    const [imageName, setImageName] = useState('');
    const [uploadStatus, setUploadStatus] = useState('');
    const [searchImageId, setSearchImageId] = useState('');
    const [searchedImage, setSearchedImage] = useState('');
    const [searchStatus, setSearchStatus] = useState('');
    const [loading, setLoading] = useState(false);

    // Xử lý chọn file ảnh để tải lên
    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setImage({ file, url: imageUrl });
            console.log('Selected image:', file);
        }
    };

    // Xử lý thay đổi tên ảnh
    const handleImageNameChange = (e) => {
        setImageName(e.target.value);
    };

    // Xử lý tải ảnh lên
    const handleUpload = async () => {
        if (!image || !imageName) {
            setUploadStatus('Vui lòng chọn hình ảnh và nhập tên hình ảnh.');
            return;
        }

        const formData = new FormData();
        formData.append('image', image.file);
        formData.append('imageName', imageName);

        try {
            console.log('Sending request to: http://localhost:8888/img/upload-image');
            const response = await axios.post('http://localhost:8888/img/upload-image', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });
            console.log('Response status:', response.status);
            console.log('Response data:', response.data);
            if (response.status === 200 && response.data.code === 1000) {
                setUploadStatus('Tải lên thành công! Xem danh sách ảnh để kiểm tra.');
            } else {
                setUploadStatus(`Lỗi: ${response.data.message || 'Tải lên thất bại.'}`);
            }
        } catch (error) {
            console.error('Error details:', error);
            setUploadStatus('Lỗi: Không thể kết nối đến server.');
        }
    };

    // Xử lý thay đổi ID ảnh cần tìm
    const handleSearchImageIdChange = (e) => {
        setSearchImageId(e.target.value);
    };

    // Xử lý tìm kiếm ảnh theo ID
    const handleSearch = async () => {
        if (!searchImageId) {
            setSearchStatus('Vui lòng nhập ID ảnh để tìm.');
            return;
        }
        setLoading(true);
        setSearchStatus('');
        setSearchedImage('');
        try {
            const response = await axios.get(`http://localhost:8888/img/${searchImageId}`, {
                withCredentials: true,
            });
            const data = response.data;
            if (data.code === 1000 && data.data.imageData) {
                setSearchedImage(`data:image/jpeg;base64,${data.data.imageData}`);
                setSearchStatus('Tìm ảnh thành công!');
            } else if (response.status === 403) {
                setSearchStatus('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
                setTimeout(() => setPage('login'), 2000);
            } else {
                setSearchStatus(data.message || 'Không tìm thấy ảnh.');
            }
        } catch (error) {
            console.error('Error details:', error);
            setSearchStatus('Lỗi sammen kết nối đến server.');
        } finally {
            setLoading(false);
        }
    };

    // Xử lý khi nhấn Enter để tìm
    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <div className="form-container min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-lg">
                <h2 className="text-2xl font-bold text-gray-800 mb-4 text-center">
                    Tải Lên và Tìm Hình Ảnh
                </h2>

                {/* Phần tải lên ảnh */}
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
                        className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                {image && (
                    <div className="preview">
                        <p>Xem trước:</p>
                        <img src={image.url} alt="Preview" style={{ maxWidth: '300px' }} />
                    </div>
                )}
                <button onClick={handleUpload} className="upload-button w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition">
                    Tải Lên
                </button>
                {uploadStatus && (
                    <p className={uploadStatus.includes('thành công') ? 'success text-green-500 mt-2 text-center' : 'error text-red-500 mt-2 text-center'}>
                        {uploadStatus}
                    </p>
                )}

                {/* Phần tìm kiếm ảnh theo ID */}
                <div className="form-group mt-6">
                    <label>Tìm ảnh theo ID</label>
                    <input
                        type="text"
                        value={searchImageId}
                        onChange={handleSearchImageIdChange}
                        onKeyPress={handleKeyPress}
                        placeholder="Nhập ID ảnh (ví dụ: tên-file-gốc_123456789)"
                        className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <button
                    onClick={handleSearch}
                    disabled={loading}
                    className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition disabled:bg-gray-400"
                >
                    {loading ? 'Đang tìm...' : 'Tìm Ảnh'}
                </button>
                {searchStatus && (
                    <p className={searchStatus.includes('thành công') ? 'success text-green-500 mt-2 text-center' : 'error text-red-500 mt-2 text-center'}>
                        {searchStatus}
                    </p>
                )}
                {searchedImage && (
                    <div className="preview mt-4">
                        <p>Ảnh tìm được:</p>
                        <img src={searchedImage} alt="Ảnh từ MinIO" style={{ maxWidth: '300px' }} />
                    </div>
                )}

                {/* Nút xem tất cả ảnh */}
                <button
                    onClick={() => {
                        console.log('Nút Xem Tất Cả Ảnh được nhấn, chuyển sang imageList');
                        setPage('imageList');
                    }}
                    className="w-full mt-4 bg-green-500 text-white p-2 rounded hover:bg-green-600 transition"
                >
                    Xem Tất Cả Ảnh
                </button>

                {/* Nút quay lại */}
                <button
                    onClick={() => setPage('login')}
                    className="back-button w-full mt-4 bg-gray-500 text-white p-2 rounded hover:bg-gray-600 transition"
                >
                    Quay Lại Đăng Nhập
                </button>
            </div>
        </div>
    );
}

export default ImageUpload;