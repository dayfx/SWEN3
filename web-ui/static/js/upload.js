document.getElementById('uploadForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const title = document.getElementById('documentTitle').value;
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    if (!file) {
        showMessage('Please select a file!', 'danger');
        return;
    }

    // Validate file size (10MB max)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
        showMessage('File is too large! Maximum size is 10MB.', 'danger');
        return;
    }

    // Create FormData object for multipart/form-data
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    // author is optional

    try {
        const response = await fetch('/api/documents', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const uploadedDoc = await response.json();
            showMessage('Document successfully uploaded!', 'success');
            console.log('Uploaded document:', uploadedDoc);

            // Reset form after successful upload
            document.getElementById('uploadForm').reset();
        } else if (response.status === 400) {
            showMessage('Upload failed! Please check your file type and size.', 'danger');
        } else {
            showMessage('Upload failed! Server error.', 'danger');
        }
    } catch (error) {
        console.error('Upload error:', error);
        showMessage('Upload failed! Network error.', 'danger');
    }
});

function showMessage(text, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.className = `alert alert-${type} mt-3`;
    messageDiv.textContent = text;
    messageDiv.style.display = 'block';
}
