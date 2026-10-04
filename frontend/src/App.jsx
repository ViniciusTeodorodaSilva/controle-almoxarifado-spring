import { Routes, Route, Navigate, Link } from 'react-router-dom'
import AppShell from './components/AppShell'
import Dashboard from './pages/Dashboard'
import Products from './pages/Products'
import Registries from './pages/Registries'
import { Stocks, Requests, Movements } from './pages/Operations'
export default function App(){return <Routes><Route element={<AppShell/>}><Route index element={<Navigate to="/dashboard" replace/>}/><Route path="dashboard" element={<Dashboard/>}/><Route path="produtos" element={<Products/>}/><Route path="categorias" element={<Registries key="categorias" name="categorias"/>}/><Route path="unidades" element={<Registries key="unidades" name="unidades-medida"/>}/><Route path="almoxarifados" element={<Registries key="almoxarifados" name="almoxarifados"/>}/><Route path="funcionarios" element={<Registries key="funcionarios" name="funcionarios"/>}/><Route path="estoques" element={<Stocks/>}/><Route path="solicitacoes" element={<Requests/>}/><Route path="movimentacoes" element={<Movements/>}/><Route path="*" element={<div className="state"><p className="eyebrow">404</p><h1>Página não encontrada</h1><p>O endereço informado não corresponde a uma página da BES.</p><Link to="/dashboard" className="btn">Ir para o dashboard</Link></div>}/></Route></Routes>}
