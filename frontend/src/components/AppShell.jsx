import { useAuth } from '../auth/AuthContext'
import { routePermission } from '../auth/permissions'
import Brand from './Brand'
import { useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { LayoutDashboard, ClipboardList, Warehouse, ArrowLeftRight, Package, Tags, Ruler, Building2, Users, HardHat, ShoppingCart, Wrench, ClipboardCheck, Menu, X, ChevronRight } from 'lucide-react'
const groups = [
  ['VISÃO GERAL', [['/dashboard','Dashboard',LayoutDashboard]]],
  ['OPERAÇÃO', [['/solicitacoes','Solicitações',ClipboardList],['/estoques','Estoque',Warehouse],['/transferencias','Transferências',ArrowLeftRight],['/movimentacoes','Movimentações',ArrowLeftRight]]],
  ['COMPRAS', [['/necessidades-compra','Necessidades de compra',ShoppingCart],['/pedidos-compra','Pedidos de compra',ShoppingCart],['/fornecedores','Fornecedores',Building2]]],
  ['CATÁLOGO', [['/produtos','Produtos',Package],['/categorias','Categorias',Tags],['/unidades','Unidades de medida',Ruler]]],
  ['ESTRUTURA', [['/obras','Obras',HardHat],['/ordens-servico','Ordens de serviço',ClipboardList],['/centros-custo','Centros de custo',Building2],['/almoxarifados','Almoxarifados',Building2],['/funcionarios','Funcionários',Users]]],
  ['PRÓXIMOS MÓDULOS', [[null,'Ferramentas',Wrench],[null,'Inventário',ClipboardCheck]]]
]
export default function AppShell() {
  const [open, setOpen] = useState(false)
  const auth = useAuth()
  const [logoutError, setLogoutError] = useState('')
  const location = useLocation()
  const current = location.pathname === '/usuarios' ? 'Usuários' : groups.flatMap(group => group[1]).find(item => item[0] === location.pathname)?.[1] || 'BES'
  return <div className="app-shell"><a className="skip-link" href="#content">Ir para conteúdo</a>
    {open && <button className="sidebar-backdrop" aria-label="Fechar menu" onClick={() => setOpen(false)}/>}
    <aside className={`sidebar ${open ? 'is-open' : ''}`} aria-label="Menu principal">
      <div className="brand"><Brand/><p>Gestão Operacional</p><button className="icon-button mobile-close" aria-label="Fechar menu" onClick={() => setOpen(false)}><X/></button></div>
      <nav>{groups.map(([label, items]) => <div className="nav-group" key={label}><p>{label}</p>{items.filter(([path])=>!path || !routePermission[path] || auth.can(routePermission[path])).map(([path, name, Icon]) => path ? <NavLink to={path} key={name} onClick={() => setOpen(false)} className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}><Icon size={18}/><span>{name}</span></NavLink> : <div className="nav-link upcoming" key={name}><Icon size={18}/><span>{name}</span><small>Em breve</small></div>)}</div>)}{auth.can('USUARIO_GERENCIAR')&&<div className="nav-group"><p>ADMINISTRAÇÃO</p><NavLink className={({isActive})=>`nav-link ${isActive?'active':''}`} to="/usuarios" onClick={()=>setOpen(false)}><Users size={18}/><span>Usuários</span></NavLink></div>}</nav>
      <div className="sidebar-footer"><span className="status-dot"/>Núcleo operacional<span className="version">v0.1</span></div>
    </aside>
    <div className="workspace"><header className="topbar"><div className="flex items-center gap-3"><button className="icon-button menu-toggle" aria-label="Abrir menu" aria-expanded={open} onClick={() => setOpen(true)}><Menu/></button><span className="breadcrumb">BES <ChevronRight size={14}/> <strong>{current}</strong></span></div><div className="user-menu"><span><strong>{auth.user.nomeExibicao}</strong><small>{auth.user.perfil}</small></span><button className="btn text" onClick={async()=>{try{await auth.logout()}catch{setLogoutError('Não foi possível sair. Tente novamente.')}}}>Sair</button>{logoutError&&<span role="alert">{logoutError}</span>}</div></header>
      <main id="content" className="main-content"><Outlet/></main><footer className="page-footer">BES · Plataforma de Gestão Operacional<span></span></footer>
    </div></div>
}
