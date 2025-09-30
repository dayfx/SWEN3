document.getElementById('uploadForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const title = document.getElementById('documentTitle').value;
    const file = document.getElementById('fileInput').files[0];

    if (!file) {
        document.getElementById('message').innerHTML =
            '<div class="alert alert-danger" role="alert">Please select a file!</div>';
        return;
    }

    try {
        // Read file content as text
        const content = await readFileAsText(file);

        fetch('/api/documents', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                title: title,
                author: 'Uploaded User',
                content: content
            })
        })
            .then(response => {
                if (response.ok) {
                    document.getElementById('message').innerHTML =
                        '<div class="alert alert-success" role="alert">Document successfully uploaded!</div>';
                    document.getElementById('uploadForm').reset();
                } else {
                    throw new Error('Upload failed!');
                }
            })
            .catch(error => {
                document.getElementById('message').innerHTML =
                    '<div class="alert alert-danger" role="alert">Document upload failed!</div>';
            });

    } catch (error) {
        document.getElementById('message').innerHTML =
            '<div class="alert alert-danger" role="alert">Error reading file!</div>';
    }
});

function readFileAsText(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.onerror = (e) => reject(e);
        reader.readAsText(file);
    });
}