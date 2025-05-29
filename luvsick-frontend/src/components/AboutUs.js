import React from 'react';
import './AboutUs.css';

const AboutUs = () => {
  return (
    <div className="about-us-container">
      <div className="about-us-header">
        <h1>LUVSICK</h1>
        <p className="tagline">Streetwear Culture • Limited Drops • Premium Quality</p>
      </div>

      <div className="about-us-content">
        <section className="mission-section">
          <h2>Our Vision</h2>
          <p>
            Born in the heart of Alexandria, LUVSICK is more than just a streetwear brand - we're a movement.
            We're redefining Egyptian street culture through limited-edition drops that blend premium quality
            with bold, contemporary designs. Each piece tells a story, each drop creates a moment.
          </p>
        </section>

        <section className="story-section">
          <h2>Our Story</h2>
          <p>
            From the vibrant streets of Alexandria to every corner of Egypt, LUVSICK emerged from a passion
            for authentic street culture and premium fashion. We're not just making clothes - we're crafting
            statements. Our limited drops aren't just products; they're collectible pieces of street culture
            that you won't find anywhere else.
          </p>
        </section>

        <section className="values-section">
          <h2>What We Stand For</h2>
          <div className="values-grid">
            <div className="value-card">
              <h3>Limited Edition</h3>
              <p>Exclusive drops that make you stand out. Once they're gone, they're gone.</p>
            </div>
            <div className="value-card">
              <h3>Premium Quality</h3>
              <p>We never compromise on materials or craftsmanship. Every piece is made to last.</p>
            </div>
            <div className="value-card">
              <h3>Street Culture</h3>
              <p>Authentic streetwear that represents the real Egypt.</p>
            </div>
            <div className="value-card">
              <h3>Nationwide Delivery</h3>
              <p>Bringing premium streetwear to every corner of Egypt.</p>
            </div>
          </div>
        </section>

        <section className="drops-section">
          <h2>Our Drops</h2>
          <p>
            Each LUVSICK drop is carefully curated and limited in quantity. We believe in creating
            exclusive pieces that you won't see on everyone. Our drops are more than just clothing
            - they're a statement of individuality and style. Stay tuned to our social media for
            upcoming drops and be ready to secure your piece of street culture.
          </p>
        </section>

        <section className="audience-section">
          <h2>Our Community</h2>
          <p>
            LUVSICK is crafted for those who appreciate the finer things in life while staying true to street culture.
            Our pieces are designed for the discerning individual who values exclusivity and premium quality.
            Whether you're in Alexandria's upscale neighborhoods or anywhere across Egypt, we bring our
            limited-edition drops directly to you.
          </p>
        </section>

        <section className="contact-section">
          <h2>Connect With Us</h2>
          <div className="contact-grid">
            <div className="contact-card">
              <h3>Email</h3>
              <a href="mailto:luvsick@gmail.com">luvsick@gmail.com</a>
            </div>
            <div className="contact-card">
              <h3>Phone</h3>
              <a href="tel:+2010000906333">010000906333</a>
            </div>
            <div className="contact-card">
              <h3>Location</h3>
              <p>Alexandria, Egypt</p>
            </div>
            <div className="contact-card">
              <h3>Delivery</h3>
              <p>Nationwide Shipping Available</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default AboutUs; 