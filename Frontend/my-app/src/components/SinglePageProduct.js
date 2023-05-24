import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom'

export default function SingleProduct(props){

    const { productId } = useParams()
    const [productPage, setProductPage] = useState()

    /* When the user clicks on a product, it collects that product's data and saves it to productPage state */
    async function singlePageProduct(){
        const res = await fetch(`http://localhost:8080/products/${productId}`)
        const data = await res.json()
        setProductPage(data)
        document.title = `${data.name} | Greenbay`
    }
    
    useEffect(() => {
        singlePageProduct()
    }, [])

    function setUserTitle(){
        document.title = `${productPage.uploader} | Greenbay`
    }

    if(productPage != undefined){
    return (
        <div className="single-product">

            <div>
            <Link to="/">Back to Home Page</Link>
            <h1>{productPage.name}</h1>
            <img src={productPage.photoUrl} alt={productPage.name} />
            </div>

            <div className="single-product-informations">
            <Link to={`/user/${productPage.uploader}`} className="user-link" onClick={setUserTitle}>Uploaded by: {productPage.uploader}</Link>
            <h3>Purchase Price: ${productPage.purchasePrice}</h3>
            <h3>{productPage.startingPrice && "Staring Price: $" + productPage.startingPrice}</h3>
            <h3>Description</h3>

            <p>{productPage.description}</p>
            <h3>{productPage.expiresAt && "Auction ends at: " + productPage.expiresAt}</h3>
            
            <button className="purchase-btn" onClick={() => props.purchase(productId)}>Purchase</button>
            </div>

        </div>
    )
    }
}