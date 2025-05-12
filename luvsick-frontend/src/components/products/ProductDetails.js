import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useParams, useLocation } from 'react-router-dom';
import axios from '../../utils/axios';
import { useCart } from '../../context/CartContext';
import { useProducts } from '../../context/ProductContext';

const Page = styled.div`
  display: flex;
  min-height: 100vh;
  background: #fff;
  @media (max-width: 900px) {
    flex-direction: column;
  }
`;

const ImageSection = styled.div`
  flex: 6;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  min-height: 100vh;
  height: 100vh;
  @media (max-width: 900px) {
    min-height: 320px;
    height: 320px;
    flex: none;
  }
  overflow: hidden;
`;

const ProductImage = styled.img`
  width: 100%;
  height: 100%;
  max-width: none;
  max-height: none;
  object-fit: cover;
  display: block;
  background: none;
`;

const InfoSection = styled.div`
  flex: 4;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 4rem 3rem 4rem 3rem;
  @media (max-width: 900px) {
    padding: 2rem 1rem;
    width: 100%;
  }
`;

const Title = styled.h1`
  font-size: 2.2rem;
  font-weight: 700;
  color: #111;
  margin-bottom: 1.5rem;
`;

const PriceRow = styled.div`
  display: flex;
  align-items: baseline;
  gap: 1.2rem;
  margin-bottom: 1.5rem;
`;

const Price = styled.div`
  font-size: 1.5rem;
  font-weight: 700;
  color: #111;
`;

const OldPrice = styled.span`
  color: #888;
  text-decoration: line-through;
  font-size: 1.1rem;
`;

const Discount = styled.span`
  color: #ff4b4b;
  font-size: 1.1rem;
  font-weight: 600;
`;

const SizeLabel = styled.div`
  font-weight: 600;
  margin-bottom: 0.7rem;
  margin-top: 2rem;
`;

const SizePills = styled.div`
  display: flex;
  gap: 0.7rem;
  margin-bottom: 1.5rem;
`;

const SizePill = styled.button`
  padding: 0.5rem 1.3rem;
  border-radius: 999px;
  border: 1.5px solid #111;
  background: ${({ active }) => (active ? '#111' : '#fff')};
  color: ${({ active }) => (active ? '#fff' : '#111')};
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  outline: none;
  &:hover {
    background: #111;
    color: #fff;
  }
`;

const QuantityRow = styled.div`
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
`;

const QuantityButton = styled.button`
  background: #f8f9fa;
  border: 1px solid #eee;
  border-radius: 6px;
  font-size: 1.2rem;
  width: 32px;
  height: 32px;
  cursor: pointer;
  &:hover {
    background: #111;
    color: #fff;
  }
`;

const AddToCartButton = styled.button`
  background: #111;
  color: #fff;
  font-weight: 700;
  font-size: 1.1rem;
  padding: 1rem 0;
  border-radius: 0;
  border: none;
  margin-top: 1.5rem;
  cursor: pointer;
  width: 100%;
  letter-spacing: 0.05em;
  transition: background 0.2s;
  &:hover {
    background: #222;
  }
`;

const Divider = styled.hr`
  border: none;
  border-top: 1px solid #eee;
  margin: 2.5rem 0 1.5rem 0;
`;

const Description = styled.div`
  font-size: 1.1rem;
  color: #444;
  line-height: 1.7;
`;

const ProductDetails = () => {
  const { id } = useParams();
  const location = useLocation();
  const { addToCart } = useCart();
  const { products } = useProducts();
  const [product, setProduct] = useState(location.state?.product || null);
  const [selectedSize, setSelectedSize] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(!location.state?.product);
  const [error, setError] = useState('');

  useEffect(() => {
    if (product) {
      if (product.productSizeDTOS && product.productSizeDTOS.length > 0) {
        setSelectedSize(product.productSizeDTOS[0].name || product.productSizeDTOS[0].size);
      }
      setLoading(false);
      return;
    }
    // Try to find product in context
    const found = products.find(p => String(p.id) === String(id));
    if (found) {
      setProduct(found);
      if (found.productSizeDTOS && found.productSizeDTOS.length > 0) {
        setSelectedSize(found.productSizeDTOS[0].name || found.productSizeDTOS[0].size);
      }
      setLoading(false);
    } else {
      setError('Product not found. Please return to the product list.');
      setLoading(false);
    }
  }, [id, product, products]);

  if (loading) return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  if (error) return <div style={{ padding: '2rem', color: 'red', textAlign: 'center' }}>{error}</div>;
  if (!product) return null;

  const priceValue = parseFloat(product.price) || 0;
  const discountValue = parseFloat(product.discount) || 0;
  const discountedPrice = priceValue - (priceValue * discountValue / 100);
  const sizeOptions = product?.productSizeDTOS?.map(sizeObj => sizeObj.name || sizeObj.size) || [];

  // Helper to get max stock for the selected size
  const getMaxStock = () => {
    if (!product || !product.productSizeDTOS || !selectedSize) return null;
    const sizeObj = product.productSizeDTOS.find(
      s => (s.size || s.name)?.toLowerCase() === selectedSize?.toLowerCase()
    );
    return sizeObj ? sizeObj.quantity : null;
  };
  const maxStock = getMaxStock();

  const handleAddToCart = () => {
    if (!selectedSize) return;
    // Find the selected size object to get its id (UUID)
    const selectedSizeObj = product.productSizeDTOS.find(
      s => (s.size || s.name)?.toLowerCase() === selectedSize?.toLowerCase()
    );
    addToCart({
      productId: product.id,
      name: product.name,
      image: `/api/v1/product/image/${product.id}`,
      price: priceValue,
      discount: discountValue,
      size: selectedSize,
      sizeId: selectedSizeObj?.id,
      quantity,
      stock: selectedSizeObj?.quantity
    });
    // Optionally show a toast or redirect to cart
  };

  return (
    <Page>
      <ImageSection>
        <ProductImage src={`http://localhost:8080/api/v1/product/image/${product.id}`} alt={product.name} />
      </ImageSection>
      <InfoSection>
        <Title>{product.name}</Title>
        <PriceRow>
          {discountValue > 0 && <OldPrice>LE {priceValue.toFixed(2)}</OldPrice>}
          <Price>LE {discountedPrice.toFixed(2)}</Price>
          {discountValue > 0 && <Discount>-{discountValue}%</Discount>}
        </PriceRow>
        <SizeLabel>Size</SizeLabel>
        <SizePills>
          {sizeOptions.length > 0 ? (
            sizeOptions.map(size => (
              <SizePill
                key={size}
                active={selectedSize === size}
                onClick={() => setSelectedSize(size)}
              >
                {size}
              </SizePill>
            ))
          ) : (
            <span style={{ color: '#888' }}>No sizes available</span>
          )}
        </SizePills>
        <QuantityRow>
          <span style={{ fontWeight: 600 }}>Quantity</span>
          {maxStock === 0 ? (
            <span style={{ color: 'red', fontWeight: 600, marginLeft: 12 }}>Out of stock</span>
          ) : (
            <>
              <QuantityButton
                onClick={() => setQuantity(q => Math.max(1, q - 1))}
                disabled={quantity <= 1 || maxStock === null}
              >-</QuantityButton>
              <span>{quantity}</span>
              <QuantityButton
                onClick={() => setQuantity(q => Math.min(maxStock, q + 1))}
                disabled={maxStock === null || quantity >= maxStock}
              >+</QuantityButton>
            </>
          )}
        </QuantityRow>
        <div style={{ color: '#888', fontSize: '1rem', marginBottom: 8 }}>
          In stock: {maxStock === null ? 'Unknown' : maxStock === 0 ? 'Out of stock' : maxStock}
        </div>
        
        <AddToCartButton 
          onClick={handleAddToCart} 
          disabled={!selectedSize || maxStock === 0}
          style={{ 
            background: (!selectedSize || maxStock === 0) ? '#ccc' : '#111',
            cursor: (!selectedSize || maxStock === 0) ? 'not-allowed' : 'pointer'
          }}
        >
          {!selectedSize ? 'Select a size' : maxStock === 0 ? 'Out of stock' : 'Add to Cart'}
        </AddToCartButton>
        <Divider />
        <Description>
          <span style={{ fontWeight: 600, display: 'block', marginBottom: 8 }}>Description</span>
          {product.description}
        </Description>
      </InfoSection>
    </Page>
  );
};

export default ProductDetails; 