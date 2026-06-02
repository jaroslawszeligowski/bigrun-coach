export default function MountainRunner() {
  return (
    <div className="mountain-runner-wrap">
      <div className="mountain-runner-scene">

        {/* Scrolling mountains — move right (runner goes left) */}
        <div className="mountain-scroll">
          <svg width="600" height="48" viewBox="0 0 600 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0 40 L30 10 L60 25 L90 5 L120 20 L150 8 L180 28 L210 12 L240 30 L270 6 L300 22 L330 10 L360 25 L390 5 L420 20 L450 8 L480 28 L510 12 L540 30 L570 6 L600 22" stroke="#cbd5e1" strokeWidth="1" fill="none"/>
            <path d="M0 48 L0 35 L40 14 L70 28 L110 8 L140 22 L180 5 L210 18 L250 10 L280 30 L320 12 L350 26 L390 8 L420 20 L460 6 L490 24 L530 14 L560 28 L600 10 L600 48 Z" fill="#e2e8f0"/>
            <path d="M0 48 L0 42 L50 22 L80 34 L120 18 L160 36 L200 20 L240 38 L280 24 L320 40 L360 22 L400 36 L440 18 L480 34 L520 20 L560 38 L600 24 L600 48 Z" fill="#cbd5e1"/>
          </svg>
        </div>

        {/* Ground */}
        <div className="mountain-ground" />

        {/* Runner — side view facing left */}
        <div className="mountain-runner-figure">
          <svg width="18" height="24" viewBox="0 0 18 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            {/* Head */}
            <circle cx="6" cy="3" r="2.2" fill="#fc4c02"/>
            {/* Body leaning forward */}
            <line x1="6" y1="5.2" x2="5" y2="11" stroke="#fc4c02" strokeWidth="1.6" strokeLinecap="round"/>
            {/* Arm back (right) */}
            <line x1="5.5" y1="7" x2="9" y2="9.5" stroke="#fc4c02" strokeWidth="1.4" strokeLinecap="round"/>
            {/* Arm forward (left) */}
            <line x1="5.5" y1="7" x2="2" y2="5.5" stroke="#fc4c02" strokeWidth="1.4" strokeLinecap="round"/>
            {/* Leg forward */}
            <line x1="5" y1="11" x2="2" y2="16" stroke="#fc4c02" strokeWidth="1.6" strokeLinecap="round"/>
            <line x1="2" y1="16" x2="0" y2="19" stroke="#fc4c02" strokeWidth="1.4" strokeLinecap="round"/>
            {/* Leg back (bent) */}
            <line x1="5" y1="11" x2="8" y2="15" stroke="#fc4c02" strokeWidth="1.6" strokeLinecap="round"/>
            <line x1="8" y1="15" x2="7" y2="11" stroke="#fc4c02" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
        </div>

      </div>
      <span className="mountain-runner-label">analyzing your question...</span>
    </div>
  );
}
