import React from 'react';
import { renderToString } from 'react-dom/server';
import Home from './Home';

describe('Home page', () => {
  test('muestra la bienvenida principal', () => {
    const html = renderToString(<Home />);
    expect(html).toContain('Bienvenido al Colegio Bernardo O’Higgins');
    expect(html).toContain('Conoce Más');
  });
});
