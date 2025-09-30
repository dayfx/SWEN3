// Auto-load documents when page loads
document.addEventListener('DOMContentLoaded', loadDocuments);
// Modal instance
const documentModal = new bootstrap.Modal(document.getElementById('documentModal'));

function loadDocuments() {
    fetch('/api/documents')
        .then(response => response.json())
        .then(data => {
            const documentsTableBody = document.getElementById('documentsTableBody');
            documentsTableBody.innerHTML = '';
            if (data.length === 0) {
                const row = `<tr><td colspan="4" class="text-center text-muted">No documents found.</td></tr>`;
                documentsTableBody.innerHTML = row;
                return;
            }
            data.forEach((document) => {
                const uploadDate = document.uploadDate ? new Date(document.uploadDate).toLocaleDateString() : 'N/A';
                const row = `
                        <tr>
                            <th scope="row">${document.id}</th>
                            <td>${document.title}</td>
                            <td>${uploadDate}</td>
                            <td>
                                <button class="btn btn-primary btn-sm" onclick="viewDocument(${document.id})">View</button>
                                <button class="btn btn-danger btn-sm" onclick="deleteDocument(${document.id})">Delete</button>
                            </td>
                        </tr>
                    `;
                documentsTableBody.insertAdjacentHTML('beforeend', row);
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
            document.getElementById('modalDocumentContent').textContent = doc.content;

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
                    loadDocuments(); // Reload the list
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