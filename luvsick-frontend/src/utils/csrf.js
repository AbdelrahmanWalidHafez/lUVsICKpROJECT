import axios from './axios';

export const refreshCsrfToken = async () => {
  try {
    await axios.get('/api/v1/product/allProducts');
  } catch (err) {
    // Optionally handle error
  }
}; 