import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import Contacto from './Contacto';

describe('Contacto page', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('debe mostrar el formulario de contacto con todos los campos', () => {
    render(<Contacto />);
    
    expect(screen.getByText('Contacto')).toBeInTheDocument();
    expect(screen.getByText('Enviar mensaje')).toBeInTheDocument();
    expect(screen.getByLabelText('Nombre')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Mensaje')).toBeInTheDocument();
  });

  it('debe mostrar la información de contacto del colegio', () => {
    render(<Contacto />);
    
    expect(screen.getByText(/Dirección:/)).toBeInTheDocument();
    expect(screen.getByText('Av. Principal 123, Santiago')).toBeInTheDocument();
    expect(screen.getByText(/Teléfono:/)).toBeInTheDocument();
    expect(screen.getByText('+56 9 1234 5678')).toBeInTheDocument();
    expect(screen.getByText(/Horario:/)).toBeInTheDocument();
  });

  it('debe mostrar botones de redes sociales', () => {
    render(<Contacto />);
    
    expect(screen.getByText('Instagram')).toBeInTheDocument();
    expect(screen.getByText('WhatsApp')).toBeInTheDocument();
    expect(screen.getByText('Facebook')).toBeInTheDocument();
  });

  it('debe validar que el nombre sea requerido', async () => {
    render(<Contacto />);
    
    const button = screen.getByText('Enviar mensaje');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Ingresa tu nombre')).toBeInTheDocument();
    });
  });

  it('debe validar que el email sea válido', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'email-inválido' } });
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Email inválido')).toBeInTheDocument();
    });
  });

  it('debe validar que el mensaje tenga al menos 10 caracteres', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Hola' } });
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Escribe al menos 10 caracteres')).toBeInTheDocument();
    });
  });

  it('debe enviar el formulario con datos válidos', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Este es un mensaje de prueba' } });
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Mensaje enviado')).toBeInTheDocument();
    });
  });

  it('debe guardar el mensaje en localStorage', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Este es un mensaje de prueba' } });
    fireEvent.click(button);

    await waitFor(() => {
      const savedData = JSON.parse(localStorage.getItem('reportes_contacto'));
      expect(savedData).toBeDefined();
      expect(savedData[0].nombre).toBe('Juan Pérez');
      expect(savedData[0].correo).toBe('juan@example.com');
      expect(savedData[0].mensaje).toBe('Este es un mensaje de prueba');
    });
  });

  it('debe limpiar el formulario después de enviar', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Este es un mensaje de prueba' } });
    fireEvent.click(button);

    await waitFor(() => {
      expect(nameInput.value).toBe('');
      expect(emailInput.value).toBe('');
      expect(messageInput.value).toBe('');
    });
  });

  it('debe generar IDs únicos para cada mensaje', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Primer mensaje de prueba' } });
    fireEvent.click(button);

    await waitFor(() => {
      const savedData = JSON.parse(localStorage.getItem('reportes_contacto'));
      expect(savedData.length).toBe(1);
    });
  });

  it('debe guardar la fecha de creación en los mensajes guardados', async () => {
    render(<Contacto />);
    
    const nameInput = screen.getByLabelText('Nombre');
    const emailInput = screen.getByLabelText('Email');
    const messageInput = screen.getByLabelText('Mensaje');
    const button = screen.getByText('Enviar mensaje');

    fireEvent.change(nameInput, { target: { value: 'Juan Pérez' } });
    fireEvent.change(emailInput, { target: { value: 'juan@example.com' } });
    fireEvent.change(messageInput, { target: { value: 'Este es un mensaje de prueba' } });
    fireEvent.click(button);

    await waitFor(() => {
      const savedData = JSON.parse(localStorage.getItem('reportes_contacto'));
      expect(savedData[0].createdAt).toBeDefined();
      expect(new Date(savedData[0].createdAt)).toBeInstanceOf(Date);
    });
  });
});
