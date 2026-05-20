import React from 'react';
import { renderToString } from 'react-dom/server';
import { MemoryRouter } from 'react-router-dom';
import Footer from './Footer';

describe('Footer component', () => {
  test('incluye enlaces rápidos y texto de copyright', () => {
    const html = renderToString(
      <MemoryRouter>
        <Footer />
      </MemoryRouter>
    );
    expect(html).toContain('Enlaces Rápidos');
    expect(html).toContain('Inicio');
    expect(html).toContain('© 2026 Colegio Bernardo O’Higgins');
  });
});
