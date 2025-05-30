import { useState } from 'react'

function ImageUpload({ setPage }) {
    const [image, setImage] = useState(null)

    const handleImageChange = (e) => {
        const file = e.target.files[0]
        if (file) {
            const imageUrl = URL.createObjectURL(file)
            setImage(imageUrl)
            console.log('Selected image:', file)
            // Thêm logic gửi ảnh đến backend nếu cần
        }
    }

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
            {image && (
                <div className="preview">
                    <p>Xem trước:</p>
                    <img src={image} alt="Preview" />
                </div>
            )}
            <button
                onClick={() => setPage('login')}
                className="back-button"
            >
                Quay Lại Đăng Nhập
            </button>
        </div>
    )
}

export default ImageUpload
