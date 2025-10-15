// Auto-load documents when page loads
document.addEventListener('DOMContentLoaded', loadDocumentsCards);
// Modal instance
const documentModal = new bootstrap.Modal(document.getElementById('documentModal'));

function loadDocumentsCards() {
    fetch('/api/documents')
        .then(response => response.json())
        .then(data => {
            const cardsContainer = document.getElementById('documentCardsContainer');
            cardsContainer.innerHTML = '';

            if (data.length === 0) {
                const message = `
                    <div class="col-12">
                        <div class="text-center p-5 bg-light rounded">
                            <h4>No documents found.</h4>
                            <p>Why not <a href="/upload.html">upload</a> one?</p>
                        </div>
                    </div>
                `;
                cardsContainer.innerHTML = message;
                return;
            }

            data.forEach((document) => {
                const uploadDate = document.uploadDate ? new Date(document.uploadDate).toLocaleDateString() : 'N/A';

                const cardHTML = `
                    <div class="col-sm-12 col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">${document.title}</h5>
                                <h6 class="card-subtitle mb-2 text-muted">Uploaded: ${uploadDate}</h6>
                                <p class="card-text small text-muted">ID: ${document.id}</p>
                                
                                <div class="mt-auto text-end">
                                    <button class="btn btn-primary btn-sm" onclick="viewDocument(${document.id})">View</button>
                                    <button class="btn btn-danger btn-sm" onclick="deleteDocument(${document.id})">Delete</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                cardsContainer.insertAdjacentHTML('beforeend', cardHTML);
            });
        })
        .catch(error => console.error('Error fetching documents:', error));
}

function viewDocument(id) {
    fetch(`/api/documents/${id}`)
        .then(response => response.json())
        .then(doc => {
            document.getElementById('documentModalLabel').textContent = doc.title;
            document.getElementById('modalDocumentAuthor').textContent = doc.author || 'Unknown';

            // Show file metadata
            const uploadDate = doc.uploadDate ? new Date(doc.uploadDate * 1000).toLocaleString() : 'N/A';
            const fileSize = doc.fileSize ? (doc.fileSize / 1024).toFixed(2) + ' KB' : 'N/A';

            const metadata = `
                <strong>Filename:</strong> ${doc.originalFilename || 'N/A'}<br>
                <strong>File Type:</strong> ${doc.mimeType || 'N/A'}<br>
                <strong>File Size:</strong> ${fileSize}<br>
                <strong>Upload Date:</strong> ${uploadDate}<br>
                <strong>Document ID:</strong> ${doc.id}<br>
                <br>
            `;

            document.getElementById('modalDocumentContent').innerHTML = metadata;

            documentModal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error loading document details.');
        });
}

/*
function viewDocument(id) {
    fetch(`/api/documents/${id}`)
        .then(response => response.json())
        .then(doc => {
            alert(`Title: ${doc.title}\nAuthor: ${doc.author}\n\nContent:\n${doc.content}`);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error loading document');
        });
*/

function deleteDocument(id) {
    if (confirm('Are you sure you want to delete this document?')) {
        fetch(`/api/documents/${id}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert('Document deleted successfully');
                    loadDocumentsCards(); // Reload the list
                } else {
                    alert('Error deleting document');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error deleting document');
            });
    }
}