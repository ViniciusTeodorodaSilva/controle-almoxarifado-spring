import { useAuth } from '../auth/AuthContext'
import { routePermission } from '../auth/permissions'
import Brand from './Brand'
import { useEffect, useRef, useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { LayoutDashboard, ClipboardList, Warehouse, ArrowLeftRight, Package, Tags, Ruler, Building2, Users, HardHat, ShoppingCart, Wrench, ClipboardCheck, Menu, X, ChevronRight } from 'lucide-react'
const groups = [
  ['VISÃO GERAL', [['/dashboard','Dashboard',LayoutDashboard]]],
  ['OPERAÇÃO', [['/solicitacoes','Solicitações',ClipboardList],['/estoques','Estoque',Warehouse],['/transferencias','Transferências',ArrowLeftRight],['/movimentacoes','Movimentações',ArrowLeftRight]]],
  ['COMPRAS', [['/necessidades-compra','Necessidades de compra',ShoppingCart],['/pedidos-compra','Pedidos de compra',ShoppingCart],['/fornecedores','Fornecedores',Building2]]],
  ['SEGURAN\u00c7A / SST', [['/epis','EPIs',HardHat],['/epi-entregas','Entregas de EPI',ClipboardList]]],
  ['ATIVOS', [['/ativos','Ferramentas e equipamentos',Wrench],['/emprestimos','Empréstimos',ClipboardList],['/transferencias-ativos','Transferências de ativos',ArrowLeftRight],['/inspecoes-ativos','Inspeções',ClipboardCheck]]],
  ['CATÁLOGO', [['/produtos','Produtos',Package],['/categorias','Categorias',Tags],['/unidades','Unidades de medida',Ruler]]],
  ['ESTRUTURA', [['/obras','Obras',HardHat],['/ordens-servico','Ordens de serviço',ClipboardList],['/centros-custo','Centros de custo',Building2],['/almoxarifados','Almoxarifados',Building2],['/funcionarios','Funcionários',Users]]],
  ['PRÓXIMOS MÓDULOS', [[null,'Inventário',ClipboardCheck]]]
]
export default function AppShell() {
  const [open, setOpen] = useState(false)
  const [mobile, setMobile] = useState(() => window.matchMedia('(max-width: 850px)').matches)
  const sidebar = useRef(null), menuButton = useRef(null)
  const resizeFocus = useRef(false)
  const sidebarFocus = useRef(false)
  const closeMenu = () => setOpen(false)
  useEffect(() => {
    const query = window.matchMedia('(max-width: 850px)')
    // Hiding the sidebar can blur its control before matchMedia fires.
    const rememberFocus = event => { sidebarFocus.current = sidebar.current.contains(event.target) }
    const resize = () => {
      resizeFocus.current = sidebar.current.contains(document.activeElement) || sidebarFocus.current
      setMobile(query.matches)
      if (!query.matches) setOpen(false)
    }
    query.addEventListener('change', resize)
    document.addEventListener('focusin', rememberFocus)
    return () => {
      query.removeEventListener('change', resize)
      document.removeEventListener('focusin', rememberFocus)
    }
  }, [])
  useEffect(() => {
    if (resizeFocus.current) {
      const target = mobile ? menuButton.current : sidebar.current.querySelector('.nav-link.active')
      target?.focus()
      resizeFocus.current = false
    }
  }, [mobile])
  useEffect(() => {
    if (!mobile || !open) return
    sidebar.current.querySelector('button').focus()
    return () => {
      const target = menuButton.current?.getClientRects().length ? menuButton.current : sidebar.current.querySelector('.nav-link.active')
      target?.focus()
    }
  }, [mobile, open])
  function keepMenuFocus(event) {
    if (!mobile || !open) return
    if (event.key === 'Escape') { event.preventDefault(); closeMenu(); return }
    if (event.key !== 'Tab') return
    const controls = [...sidebar.current.querySelectorAll('button,a[href]')].filter(element => !element.disabled && element.getClientRects().length)
    const index = controls.indexOf(document.activeElement)
    event.preventDefault()
    const next = index < 0 ? (event.shiftKey ? controls.length - 1 : 0) : (index + (event.shiftKey ? -1 : 1) + controls.length) % controls.length
    controls[next]?.focus()
  }
  const auth = useAuth()
  const [logoutError, setLogoutError] = useState('')
  const location = useLocation()
  const current = location.pathname.startsWith('/epi-funcionarios/') ? 'Ficha de EPI do funcionário' : location.pathname === '/usuarios' ? 'Usuários' : groups.flatMap(group => group[1]).find(item => item[0] && (item[0] === location.pathname || location.pathname.startsWith(`${item[0]}/`)))?.[1] || 'BES'
  return <div className="app-shell"><a className="skip-link" href="#content">Ir para conteúdo</a>
    {mobile && open && <button className="sidebar-backdrop" tabIndex={-1} aria-label="Fechar menu" onClick={closeMenu}/>}
    <aside ref={sidebar} id="bes-navigation" className={`sidebar ${open ? 'is-open' : ''}`} inert={mobile && !open} role={mobile && open ? 'dialog' : undefined} aria-modal={mobile && open ? true : undefined} aria-label="Menu principal" onKeyDown={keepMenuFocus}>
      <div className="brand"><Brand/><p>Gestão Operacional</p><button className="icon-button mobile-close" aria-label="Fechar menu" onClick={closeMenu}><X/></button></div>
      <nav>{groups.map(([label, items]) => <div className="nav-group" key={label}><p>{label}</p>{items.filter(([path])=>!path || !routePermission[path] || auth.can(routePermission[path])).map(([path, name, Icon]) => path ? <NavLink to={path} key={name} onClick={closeMenu} className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}><Icon size={18}/><span>{name}</span></NavLink> : <div className="nav-link upcoming" key={name}><Icon size={18}/><span>{name}</span><small>Em breve</small></div>)}</div>)}{auth.can('USUARIO_GERENCIAR')&&<div className="nav-group"><p>ADMINISTRAÇÃO</p><NavLink className={({isActive})=>`nav-link ${isActive?'active':''}`} to="/usuarios" onClick={closeMenu}><Users size={18}/><span>Usuários</span></NavLink></div>}</nav>
      <div className="sidebar-footer"><span className="status-dot"/>Núcleo operacional<span className="version">v0.1</span></div>
    </aside>
    <div className="workspace" inert={mobile && open}><header className="topbar"><div className="flex items-center gap-3"><button ref={menuButton} className="icon-button menu-toggle" aria-controls="bes-navigation" aria-label="Abrir menu" aria-expanded={open} onClick={() => setOpen(true)}><Menu/></button><span className="breadcrumb">BES <ChevronRight size={14}/> <strong>{current}</strong></span></div><div className="user-menu"><span><strong>{auth.user.nomeExibicao}</strong><small>{auth.user.perfil}</small></span><button className="btn text" onClick={async()=>{try{await auth.logout()}catch{setLogoutError('Não foi possível sair. Tente novamente.')}}}>Sair</button>{logoutError&&<span role="alert">{logoutError}</span>}</div></header>
      <main id="content" tabIndex={-1} className="main-content"><Outlet/></main><footer className="page-footer">BES · Plataforma de Gestão Operacional<span></span></footer>
    </div></div>
}
